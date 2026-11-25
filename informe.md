# Informe · Q3 / fila 0 · arquitectura hexagonal

| Medida | Resultado |
|---|---|
| Archivos creados | 3: `JsonlOrderRepository.java`, `JsonlOrderRepositoryTest.java`, `MuestraHoja2Runner.java` |
| Archivos modificados | 2: `OrderPersistenceAdapter.java` y `LEEME.md` |
| ¿Se modificó el archivo o la clase que calcula el total? | No: `OrderUseCaseService.java` no aparece en el diff |
| ¿Se modificó el archivo o la clase que registra el pedido? | No: es la misma clase, `OrderUseCaseService.java` |
| ¿Se tocó una prueba de la Hoja 1? | No |

Fuente: `git diff --name-status hoja-1 final` (en `evidencia.txt`).

## Predicción contra medición

El ADR-0001 predijo tres archivos creados (`JsonlOrderRepository`, `JsonlOrderRepositoryTest` y `MuestraHoja2Runner`) y dos modificados (`OrderPersistenceAdapter` y `LEEME.md`), sin tocar la clase que calcula el total, la que registra el pedido ni las pruebas de la Hoja 1. La tabla medida muestra exactamente 3 creados, 2 modificados y tres respuestas «No», de modo que la predicción acertó en todo. También se cumplió la pérdida declarada: `OrderJpaEntity`, `OrderJpaRepository` y `OrderPersistenceAdapter` siguen en el árbol sin usarse. El acierto dependía de una condición que se cuidó desde la Hoja 1: el puerto `OrderRepository` ya traía la consulta del stock consumido.

## El límite

Un cambio plausible es agregar un campo al pedido, por ejemplo una nota del cliente. Aquí obliga a tocar `Order`, `OrderJpaEntity`, `OrderPersistenceAdapter`, `JsonlOrderRepository`, `OrderRequest`, `OrderResponse`, `OrderMapper`, el caso de uso y las pruebas que construyen un pedido, porque hay dos modelos y mapeos a mano. Para ese cambio capas sería mejor: la guía hexagonal admite que si el próximo cambio es agregar un campo, capas alcanza, y en «Desventajas» reconoce que si olvidas un campo en un mapeo el compilador no avisa. Hexagonal paga cuando cambia lo que rodea al negocio, como en Q3, y no cuando cambia el propio pedido.

## Lo que la regla no ve

La violación inducida fue un `import jakarta.persistence.Entity;` en `Order.java`, dentro de `domain`: `verificar-regla.sh` la detectó, imprimió la línea 3 del archivo y salió con código 1, y después se revirtió con `git checkout`. Una violación que la regla no detecta es que `domain` importe un adaptador, por ejemplo `ProductJpaEntity`: se probó y el script salió con código 0, porque la regla 1 solo busca Spring y Jakarta y la regla 2 solo vigila `application`. Tampoco vería un uso con nombre completo y sin `import`, ya que el `grep` busca líneas de importación. La guía lo advierte cuando dice que la regla no se cumple sola, y la salida sería cerrar ese hueco con una prueba ArchUnit.
