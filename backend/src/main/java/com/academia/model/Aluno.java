package com.academia.model;
import jakarta.persistence.*;
@Entity
public class Aluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    protected Aluno() {}
    public Aluno(String nome) { this.nome = nome; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
}
