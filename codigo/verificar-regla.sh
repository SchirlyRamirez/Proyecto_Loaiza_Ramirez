#!/usr/bin/env bash
# Regla de dependencias hexagonal (ADR-0001). Se corre desde la carpeta codigo/:
#   bash verificar-regla.sh
# Regla 1: nada bajo domain importa org.springframework ni jakarta.
# Regla 2: nada bajo application importa un adapter
#          (application SI importa Spring: @Service y @Transactional; eso se acepta).
# Sale con codigo 0 si se cumple y 1 si hay una violacion.
cd "$(dirname "$0")" || exit 2
base=src/main/java/com/ejemplo/catalogo
fallo=0

echo "== Regla 1: domain no importa org.springframework ni jakarta"
if grep -rn "import org.springframework\|import jakarta" "$base/domain"; then
  echo "VIOLACION: el dominio importa infraestructura"
  fallo=1
else
  echo "OK: ningun import prohibido en domain"
fi

echo "== Regla 2: application no importa ningun adapter"
if grep -rn "import com.ejemplo.catalogo.adapter" "$base/application"; then
  echo "VIOLACION: application importa un adaptador"
  fallo=1
else
  echo "OK: ningun import de adapter en application"
fi

exit $fallo
