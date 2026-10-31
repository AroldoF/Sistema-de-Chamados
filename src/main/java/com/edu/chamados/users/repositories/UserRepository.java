package com.edu.chamados.users.repositories;

import com.edu.chamados.users.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByRegistrationAndIdNot(String registration, Long id);
    boolean existsByRegistration(String registration);
}
