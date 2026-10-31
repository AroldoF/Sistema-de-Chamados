package com.edu.chamados.tickets.entities;

import com.edu.chamados.departments.entities.Category;
import com.edu.chamados.tickets.enums.Priority;
import com.edu.chamados.tickets.enums.TicketStatus;
import com.edu.chamados.users.entities.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Entity @Table(name = "tickets")
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 255) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(nullable = false, length = 255) private String location;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private Priority priority;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private TicketStatus status;
    @Column(nullable = false) private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    @Column(columnDefinition = "TEXT") private String solution;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "requester_id", nullable = false) private UserAccount requester;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "technician_id") private UserAccount technician;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("dateTime ASC") private List<Interaction> interactions = new ArrayList<>();
}
