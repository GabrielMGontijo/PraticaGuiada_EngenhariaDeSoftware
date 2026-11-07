package com.academia.controller;
import com.academia.dto.*;
import com.academia.service.FichaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
@RestController
public class FichaController {
    private final FichaService service;
    public FichaController(FichaService service) { this.service = service; }
    @PostMapping("/fichas/salvar-e-ativar")
    public ResponseEntity<FichaResponse> salvar(@Valid @RequestBody FichaRequest request) {
        var ficha = service.salvarEAtivar(request);
        return ResponseEntity.created(URI.create("/alunos/" + ficha.alunoId() + "/ficha-ativa")).body(ficha);
    }
    @GetMapping("/alunos/{id}/ficha-ativa")
    public FichaResponse ativa(@PathVariable Long id) { return service.ativa(id); }
    @GetMapping("/alunos/{id}/historico-fichas")
    public List<FichaResponse> historico(@PathVariable Long id) { return service.historico(id); }
}
