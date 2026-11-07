package com.academia.dto;
import java.time.Instant;
import java.util.List;
public record FichaResponse(Long id, Long alunoId, boolean ativa, Instant criadaEm,
        Instant arquivadaEm, List<FichaRequest.Divisao> divisoes) {}
