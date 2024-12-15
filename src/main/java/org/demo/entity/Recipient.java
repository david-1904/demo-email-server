package org.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecipientType type;

    @ManyToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email emailReference;

}
