package com.academia.repository;
import com.academia.model.Ficha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FichaRepository extends JpaRepository<Ficha, Long> {
    Optional<Ficha> findByAluno_IdAndAtivaTrue(Long alunoId);
    List<Ficha> findByAluno_IdAndAtivaFalseOrderByCriadaEmDesc(Long alunoId);
}
