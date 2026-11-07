package com.academia.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public record FichaRequest(@NotNull @Positive Long alunoId,
        @NotEmpty List<@NotNull @Valid Divisao> divisoes) {
    public record Divisao(@NotBlank String nome, @NotEmpty List<@NotNull @Valid Item> exercicios) {}
    public record Item(@NotNull @Positive Long exercicioId,
            @NotNull @Positive Integer series,
            @NotNull @Positive Integer repeticoesMin,
            @NotNull @Positive Integer repeticoesMax,
            @NotNull @DecimalMin("0.0") BigDecimal cargaInicialKg,
            @NotNull @PositiveOrZero Integer descansoSegundos) {}
}
