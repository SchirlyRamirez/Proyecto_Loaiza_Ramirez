package com.ejemplo.catalogo.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Lo que el cliente envia. La regla RN-5 (cantidad > 0, producto existente) vive en el caso de uso. */
public record OrderRequest(
        @NotBlank(message = "El cliente es obligatorio")
        String clienteId,

        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        Integer cantidad
) {
}
