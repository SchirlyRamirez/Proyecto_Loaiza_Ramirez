# ADR-0002 · Los pedidos pasan a pedidos.jsonl detrás del puerto OrderRepository

Variante: Q3 / fila 0

- Fecha: 2026-11-24 19:25
- Estado: aceptada (el ADR-0001 se mantiene)

## Contexto
La Hoja 2 (Q3) pide que pedidos y stock consumido se guarden en `pedidos.jsonl` (un objeto JSON por línea, en el directorio de ejecución) en lugar del almacenamiento anterior, que al reiniciar el producto no vuelva a su stock inicial de 60, que el id siguiente continúe la secuencia y que los RECHAZADO también se guarden. Las pruebas de la Hoja 1 no pueden editarse para que el cambio pase. Medido con `git diff --name-status hoja-1 final`: 3 archivos creados, 2 modificados.

## Alternativas consideradas
- **Mantener JPA/H2 y exportar al archivo** — descartada porque H2 está en memoria y se pierde al reiniciar: el stock de 60 volvería a estar completo y el pedido 1 de 9 unidades desaparecería, justo lo que la línea 3 de la muestra (52 unidades, se pasa por 1) comprueba.
- **Guardar el stock consumido en un segundo archivo** — descartada porque crea dos fuentes de verdad para un solo número. El stock disponible sigue derivándose (60 menos la suma de cantidades CONFIRMADO), así que el archivo de pedidos basta y un RECHAZADO nunca consume stock.
- **Usar una librería JSON** — descartada porque el objeto es plano, de seis campos fijos, y una expresión regular lo lee; una dependencia más para este tamaño sería sobreabstracción.

## Decisión
Se agrega `JsonlOrderRepository`, un segundo adaptador del puerto de salida `OrderRepository`, y se le quita `@Component` a `OrderPersistenceAdapter` para que Spring entregue el del archivo. El caso de uso, el dominio, el controlador y las pruebas de la Hoja 1 no se tocaron. El producto sigue sembrándose en H2 en cada arranque con su stock inicial; solo el consumo persiste, en el archivo.

## Consecuencias
**Se gana:** el cambio de almacenamiento cupo en `adapter/out/persistence/jsonl` más una línea de enlace; `OrderUseCaseService` (total y registro) no aparece en el diff y ninguna prueba de la Hoja 1 fue modificada. El adaptador JPA se puede reactivar devolviéndole `@Component`. Está pendiente confirmar corriendo `mvn test` que esas pruebas pasan.

**Se pierde:** (1) el código JPA de pedidos (`OrderJpaEntity`, `OrderJpaRepository`, `OrderPersistenceAdapter`) queda sin usar en el árbol, como anticipó el ADR-0001; (2) el adaptador JSONL relee el archivo completo en cada operación y su formato lo valida una expresión regular escrita a mano, lo que no escala ni resiste dos procesos escribiendo a la vez; (3) la regla de arquitectura no vio una violación probada a propósito (`domain` importando un adaptador), así que sigue dependiendo de la disciplina del equipo.

**Veredicto sobre el ADR-0001:** ocurrió lo que dijo que se perdería (más archivos y mapeos, código JPA sin usar, regla que no se cumple sola) y la predicción acertó: 3 creados (`JsonlOrderRepository`, `JsonlOrderRepositoryTest`, `MuestraHoja2Runner`), 2 modificados (`OrderPersistenceAdapter`, `LEEME.md`) y ninguna M sobre el caso de uso ni sobre pruebas de la Hoja 1. Hoy se decidiría igual.
