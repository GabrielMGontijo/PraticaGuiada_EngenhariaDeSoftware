package com.academia;

import com.academia.dto.FichaRequest;
import com.academia.exception.*;
import com.academia.model.*;
import com.academia.repository.*;
import com.academia.service.FichaService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:fichas;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
class FichaIntegrationTest {
    @Autowired FichaService service;
    @Autowired AlunoRepository alunos;
    @Autowired ExercicioRepository exercicios;
    @Autowired FichaRepository fichas;
    @Autowired ObjectMapper mapper;
    @Autowired WebApplicationContext context;
    Long alunoId;
    Long exercicioId;

    @BeforeEach void preparar() {
        fichas.deleteAll(); alunos.deleteAll(); exercicios.deleteAll();
        alunoId = alunos.save(new Aluno("Ana")).getId();
        exercicioId = exercicios.save(new Exercicio("Agachamento")).getId();
    }
    FichaRequest request(int min, int max) {
        return new FichaRequest(alunoId, List.of(new FichaRequest.Divisao("Treino A", List.of(
                new FichaRequest.Item(exercicioId, 3, min, max, new BigDecimal("10.5"), 60)))));
    }
    @Test void ativarArquivaAnteriorEPreservaDados() {
        var antiga = service.salvarEAtivar(request(8, 12));
        var nova = service.salvarEAtivar(request(10, 15));
        assertThat(service.ativa(alunoId).id()).isEqualTo(nova.id());
        assertThat(service.ativa(alunoId).divisoes()).isEqualTo(request(10, 15).divisoes());
        var historico = service.historico(alunoId);
        assertThat(historico).hasSize(1);
        assertThat(historico.getFirst().id()).isEqualTo(antiga.id());
        assertThat(historico.getFirst().ativa()).isFalse();
        assertThat(historico.getFirst().arquivadaEm()).isNotNull();
        assertThat(historico.getFirst().divisoes()).isEqualTo(request(8, 12).divisoes());
    }
    @Test void falhaDeValidacaoPreservaFichaAtual() {
        var atual = service.salvarEAtivar(request(8, 12));
        assertThatThrownBy(() -> service.salvarEAtivar(request(15, 10))).isInstanceOf(RegraNegocioException.class);
        assertThat(service.ativa(alunoId).id()).isEqualTo(atual.id());
        assertThat(service.historico(alunoId)).isEmpty();
    }
    @Test void exigeAlunoDivisaoEExercicio() {
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(null, List.of())))
                .isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(alunoId, List.of())))
                .isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(alunoId,
                List.of(new FichaRequest.Divisao("A", List.of())))))
                .isInstanceOf(RegraNegocioException.class);
    }
    @Test void rejeitaValoresInvalidosEDivisoesRepetidas() {
        var item = new FichaRequest.Item(exercicioId, 0, 0, 12, new BigDecimal("-1"), -10);
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(alunoId,
                List.of(new FichaRequest.Divisao("A", List.of(item))))))
                .isInstanceOf(RegraNegocioException.class);
        var divisao = request(8, 12).divisoes().getFirst();
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(alunoId,
                List.of(divisao, new FichaRequest.Divisao(" treino a ", divisao.exercicios())))))
                .isInstanceOf(RegraNegocioException.class);
    }
    @Test void rejeitaReferenciasInexistentes() {
        assertThatThrownBy(() -> service.salvarEAtivar(new FichaRequest(Long.MAX_VALUE, request(8, 12).divisoes())))
                .isInstanceOf(NaoEncontradoException.class);
        exercicios.deleteAll();
        assertThatThrownBy(() -> service.salvarEAtivar(request(8, 12))).isInstanceOf(NaoEncontradoException.class);
        assertThat(fichas.count()).isZero();
    }
    @Test void consultaPorAlunoNaoMisturaFichas() {
        service.salvarEAtivar(request(8, 12));
        var outro = alunos.save(new Aluno("Bruno"));
        assertThatThrownBy(() -> service.ativa(outro.getId())).isInstanceOf(NaoEncontradoException.class);
        assertThat(service.historico(outro.getId())).isEmpty();
    }
    @Test void contratoHttp() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        var resposta = mvc.perform(post("/fichas/salvar-e-ativar")
                .contentType("application/json").content(mapper.writeValueAsString(request(8, 12))))
                .andReturn().getResponse();
        assertThat(resposta.getStatus()).isEqualTo(201);
        var perfil = mvc.perform(get(resposta.getHeader("Location"))).andReturn().getResponse();
        assertThat(perfil.getStatus()).isEqualTo(200);
        assertThat(mapper.readTree(perfil.getContentAsString()).get("ativa").asBoolean()).isTrue();
        assertThat(mvc.perform(post("/fichas/salvar-e-ativar").contentType("application/json")
                .content("{}")).andReturn().getResponse().getStatus()).isEqualTo(400);
    }
}
