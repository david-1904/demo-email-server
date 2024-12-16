package org.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.demo.entity.enums.RecipientType;

@Entity
@Data
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email emailReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false)
    private RecipientType type;

}
