package com.academia.model;
import jakarta.persistence.*;
@Entity
public class Exercicio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    protected Exercicio() {}
    public Exercicio(String nome) { this.nome = nome; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
}
