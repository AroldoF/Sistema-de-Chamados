package com.edu.chamados.tickets.repositories;

import com.edu.chamados.tickets.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    boolean existsByCategoryId(Long categoryId);
    boolean existsByCategoryDepartmentId(Long departmentId);
}
