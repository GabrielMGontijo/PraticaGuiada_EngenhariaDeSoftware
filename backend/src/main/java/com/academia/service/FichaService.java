package com.academia.service;
import com.academia.dto.*;
import com.academia.exception.*;
import com.academia.model.Ficha;
import com.academia.repository.*;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
@Service
public class FichaService {
    private final AlunoRepository alunos;
    private final ExercicioRepository exercicios;
    private final FichaRepository fichas;
    private final ObjectMapper mapper;
    private final Validator validator;
    public FichaService(AlunoRepository alunos, ExercicioRepository exercicios,
            FichaRepository fichas, ObjectMapper mapper, Validator validator) {
        this.alunos = alunos; this.exercicios = exercicios;
        this.fichas = fichas; this.mapper = mapper; this.validator = validator;
    }
    @Transactional
    public FichaResponse salvarEAtivar(FichaRequest request) {
        if (request == null || !validator.validate(request).isEmpty())
            throw new RegraNegocioException("Informe aluno, divisões e todos os campos dos exercícios com valores válidos.");
        var aluno = alunos.buscarParaAtualizar(request.alunoId())
                .orElseThrow(() -> new NaoEncontradoException("Aluno não encontrado."));
        Set<String> nomes = new HashSet<>();
        for (var divisao : request.divisoes()) {
            if (!nomes.add(divisao.nome().strip().toUpperCase(Locale.ROOT)))
                throw new RegraNegocioException("Os nomes das divisões devem ser únicos.");
            for (var item : divisao.exercicios()) {
                if (item.repeticoesMin() > item.repeticoesMax())
                    throw new RegraNegocioException("A repetição mínima não pode superar a máxima.");
                if (!exercicios.existsById(item.exercicioId()))
                    throw new NaoEncontradoException("Exercício não encontrado: " + item.exercicioId());
            }
        }
        String conteudo = mapper.writeValueAsString(request);
        fichas.findByAluno_IdAndAtivaTrue(aluno.getId()).ifPresent(Ficha::arquivar);
        return resposta(fichas.save(new Ficha(aluno, conteudo)));
    }
    @Transactional(readOnly = true)
    public FichaResponse ativa(Long alunoId) {
        verificarAluno(alunoId);
        return resposta(fichas.findByAluno_IdAndAtivaTrue(alunoId)
                .orElseThrow(() -> new NaoEncontradoException("Aluno sem ficha ativa.")));
    }
    @Transactional(readOnly = true)
    public List<FichaResponse> historico(Long alunoId) {
        verificarAluno(alunoId);
        return fichas.findByAluno_IdAndAtivaFalseOrderByCriadaEmDesc(alunoId).stream().map(this::resposta).toList();
    }
    private void verificarAluno(Long id) {
        if (!alunos.existsById(id)) throw new NaoEncontradoException("Aluno não encontrado.");
    }
    private FichaResponse resposta(Ficha ficha) {
        var dados = mapper.readValue(ficha.getConteudo(), FichaRequest.class);
        return new FichaResponse(ficha.getId(), ficha.getAlunoId(), ficha.isAtiva(),
                ficha.getCriadaEm(), ficha.getArquivadaEm(), dados.divisoes());
    }
}
