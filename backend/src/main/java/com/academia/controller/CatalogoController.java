package com.academia.controller;
import com.academia.service.CatalogoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
public class CatalogoController {
    private final CatalogoService service;
    public CatalogoController(CatalogoService service) { this.service = service; }
    @GetMapping("/alunos") public List<CatalogoService.Opcao> alunos() { return service.alunos(); }
    @GetMapping("/exercicios") public List<CatalogoService.Opcao> exercicios() { return service.exercicios(); }
}
