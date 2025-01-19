package org.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.demo.entity.enums.RecipientType;

@Entity
@Data
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipient_seq")
    @SequenceGenerator(name = "recipient_seq", sequenceName = "recipient_seq", allocationSize = 5)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false)
    private RecipientType type;

}
