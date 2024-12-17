package org.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.demo.entity.enums.EmailState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;

    @Column(name = "email_from", nullable = false)
    private String emailFrom;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Lob
    private String emailBody;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "email_status")
    private EmailState state;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private List<Recipient> recipients;

}
