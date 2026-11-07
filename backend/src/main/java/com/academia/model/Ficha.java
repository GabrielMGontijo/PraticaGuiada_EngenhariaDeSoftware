package com.academia.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
public class Ficha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Aluno aluno;
    @Lob @Column(nullable = false) private String conteudo;
    @Column(nullable = false) private boolean ativa;
    @Column(nullable = false) private Instant criadaEm;
    private Instant arquivadaEm;
    protected Ficha() {}
    public Ficha(Aluno aluno, String conteudo) {
        this.aluno = aluno; this.conteudo = conteudo; ativa = true; criadaEm = Instant.now();
    }
    public void arquivar() { ativa = false; arquivadaEm = Instant.now(); }
    public Long getId() { return id; }
    public Long getAlunoId() { return aluno.getId(); }
    public String getConteudo() { return conteudo; }
    public boolean isAtiva() { return ativa; }
    public Instant getCriadaEm() { return criadaEm; }
    public Instant getArquivadaEm() { return arquivadaEm; }
}
