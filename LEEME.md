# Tienda mínima · arquitectura hexagonal (variante Q3 / fila 0)

Integrantes: Jacobo Loaiza Castañeda y Schirly Ximena Ramírez López.
Producto: Cuaderno · precio 8.000 · stock inicial 60 · umbral u = 10 · descuento d = 5 %.

Requisitos: Java 21 y Maven (`mvn`). La persistencia es H2 en memoria (no hace falta Docker; el `compose.yaml` de la carpeta de partida queda sin usar). Todos los comandos se corren desde la carpeta `codigo/`.

## Pruebas

```
mvn test
```

## Muestra de la Hoja 1 (desde un arranque limpio: H2 en memoria arranca vacía)

```
mvn -q spring-boot:run "-Dspring-boot.run.profiles=muestra,muestra1"
```

Salida esperada:

```
PEDIDO 1 CONFIRMADO total=72000
PEDIDO 2 CONFIRMADO total=76000
PEDIDO 3 RECHAZADO total=0
PEDIDO 4 CONFIRMADO total=311600
```

La muestra llama al `OrderController` (la entrada real de la arquitectura), que pasa por el puerto `OrderService`, el caso de uso y el puerto `OrderRepository`.

## Regla de arquitectura

```
bash verificar-regla.sh
```

Verifica con `grep` que `domain` no importa `org.springframework` ni `jakarta`, y que `application` no importa ningún `adapter`. Sale con código 1 si hay una violación.
