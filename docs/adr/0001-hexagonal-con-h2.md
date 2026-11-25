# ADR-0001 · Arquitectura hexagonal con H2 en la Hoja 1

Variante: Q3 / fila 0

- Fecha: 2026-11-24 19:22
- Estado: aceptada

## Contexto
La tienda vende un solo producto, el Cuaderno (productoId 1, precio 8.000, stock inicial 60, umbral u = 10, descuento d = 5 %). Todavía no está decidido dónde vivirán los datos, y la regla de negocio (RN-1 a RN-3) no debe moverse cuando se decida. La Hoja 2 (Q3) pasará los pedidos y el stock consumido a un archivo `pedidos.jsonl` y prohíbe editar las pruebas de la Hoja 1 para que el cambio pase. El punto de partida (`Arquitectura hexagonal/catalogo`) solo trae el CRUD de `Product`, así que los pedidos se escriben desde cero. Integrantes: Jacobo Loaiza Castañeda y Schirly Ximena Ramírez López.

## Alternativas consideradas
- **Capas** — descartada porque, para Q3, el `service` de capas importa el repositorio de JPA directamente: pasar los pedidos a un archivo obligaría a tocar el servicio que calcula el total (cantidad 10 con 5 % de descuento = 76.000) y con él sus pruebas, que es justo lo que Q3 prohíbe. El cambio de esta variante es de infraestructura y no «agregar un campo», que es el caso en que la guía hexagonal dice que capas alcanza.
- **Monolito modular** — descartada porque su ventaja es aislar módulos (`catalogo`, `pedidos`) y esta tienda tiene un solo producto con stock 60 y una sola regla de descuento: no hay nada que separar. Además el cambio de Q3 es sustituir el almacenamiento, y el monolito modular no ofrece un puerto de salida explícito para eso.
- **Eventos** — descartada porque en el punto de partida el stock vive en memoria en `InventoryService` y el flujo es asíncrono. Persistirlo en `pedidos.jsonl` obliga a decidir quién escribe y cuándo, y la muestra exige un orden estricto: el pedido de 42 unidades debe rechazarse sin consumir stock antes de que el de 41 lo deje en 0. Con 4 pedidos en secuencia, la consistencia eventual solo añade riesgo.

## Decisión
Se usa arquitectura hexagonal sobre `Arquitectura hexagonal/catalogo`. El almacenamiento de pedidos es un puerto de salida `OrderRepository` en `domain/port/out`; la Hoja 1 lo implementa con JPA sobre H2 en memoria (`OrderPersistenceAdapter`) y la Hoja 2 lo sustituirá por un adaptador JSONL. El stock disponible no se guarda: se deriva como stock inicial del producto menos la suma de las cantidades de pedidos CONFIRMADO, y esa suma es una consulta del puerto desde la Hoja 1. Se elige H2 en lugar de PostgreSQL porque cada arranque ya es limpio y la muestra corre sin Docker.

## Consecuencias
**Se gana:** el cambio de almacenamiento queda confinado a `adapter/out/persistence`. Las pruebas de RN-1 a RN-3 se escriben con Mockito sobre el puerto y no saben qué hay detrás. La regla de dependencias se verifica con un `grep` (`codigo/verificar-regla.sh`). Con H2, `mvn test` y la muestra corren en cualquier máquina sin Docker.

**Se pierde:** (1) más archivos y dos modelos para pedidos (`Order` y `OrderJpaEntity`) con mapeo a mano, que se comprueba contando los archivos creados en la Hoja 1; (2) pedidos, descuento y control de stock hay que escribirlos desde cero, porque la carpeta no los trae; (3) la regla no se cumple sola: hay que ejecutarla, y `application` ya importa Spring, por lo que su regla es sobre adaptadores y no sobre Spring; (4) tras la Hoja 2 el código JPA de pedidos (`OrderJpaEntity`, `OrderJpaRepository`, `OrderPersistenceAdapter`) queda sin usar en el árbol; (5) se usa H2 y no la base de la carpeta de partida, con posibles diferencias de SQL.

**Predicción del costo de la Hoja 2:**
- Crear (3 archivos): `adapter/out/persistence/jsonl/JsonlOrderRepository.java`, `JsonlOrderRepositoryTest.java` (en `src/test`) y `adapter/in/cli/MuestraHoja2Runner.java`.
- Modificar (2 archivos): `OrderPersistenceAdapter.java` (quitarle `@Component` para que Spring entregue el adaptador JSONL) y `LEEME.md` (documentar el comando de la muestra de la Hoja 2). Total esperado en `git diff --name-status hoja-1 final`: 3 A y 2 M.
- No tocar: `OrderUseCaseService` (es la clase que calcula el total y también la que registra el pedido), `Order`, `OrderRepository`, `OrderController`, `application.properties`, `pom.xml` ni ninguna prueba de la Hoja 1. Es decir: predigo que **no** se modifica la clase que calcula el total, que **no** se modifica la que registra el pedido y que **no** se toca ninguna prueba de la Hoja 1.
- La predicción es falsa si el diff muestra una M sobre `OrderUseCaseService.java`, sobre cualquier prueba de la Hoja 1, o más de 3 A / 2 M.
