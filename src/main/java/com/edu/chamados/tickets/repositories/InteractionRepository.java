package com.edu.chamados.tickets.repositories;
import com.edu.chamados.tickets.entities.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InteractionRepository extends JpaRepository<Interaction, Long> { }
