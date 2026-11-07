package com.academia.repository;
import com.academia.model.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {}
