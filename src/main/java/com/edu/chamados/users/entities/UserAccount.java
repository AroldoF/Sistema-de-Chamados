package com.edu.chamados.users.entities;

import com.edu.chamados.departments.entities.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255) private String name;
    @Column(nullable = false, unique = true, length = 255) private String email;
    @Column(nullable = false, unique = true, length = 40) private String registration;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private UserType type;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id") private Department department;
}
