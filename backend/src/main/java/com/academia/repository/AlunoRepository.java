package com.academia.repository;
import com.academia.model.Aluno;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aluno a where a.id = :id")
    Optional<Aluno> buscarParaAtualizar(Long id);
}
