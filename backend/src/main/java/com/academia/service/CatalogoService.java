package com.academia.service;
import com.academia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class CatalogoService {
    public record Opcao(Long id, String nome) {}
    private final AlunoRepository alunos;
    private final ExercicioRepository exercicios;
    public CatalogoService(AlunoRepository alunos, ExercicioRepository exercicios) {
        this.alunos = alunos; this.exercicios = exercicios;
    }
    public List<Opcao> alunos() { return alunos.findAll().stream().map(a -> new Opcao(a.getId(), a.getNome())).toList(); }
    public List<Opcao> exercicios() { return exercicios.findAll().stream().map(e -> new Opcao(e.getId(), e.getNome())).toList(); }
}
