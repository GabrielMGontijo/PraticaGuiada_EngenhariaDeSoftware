package com.academia.repository;

import com.academia.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Catálogo inicial apenas para executar a atividade com o perfil demo. */
@Component
@Profile("demo")
public class DadosDemonstracao implements CommandLineRunner {
    private final AlunoRepository alunos;
    private final ExercicioRepository exercicios;
    public DadosDemonstracao(AlunoRepository alunos, ExercicioRepository exercicios) {
        this.alunos = alunos; this.exercicios = exercicios;
    }
    @Override @Transactional
    public void run(String... args) {
        if (alunos.count() == 0) alunos.saveAll(List.of(new Aluno("Ana"), new Aluno("Bruno")));
        if (exercicios.count() == 0) exercicios.saveAll(List.of(
                new Exercicio("Agachamento"), new Exercicio("Supino"), new Exercicio("Remada")));
    }
}
