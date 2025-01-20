package org.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.demo.entity.enums.EmailState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_seq")
    @SequenceGenerator(name = "email_seq", sequenceName = "email_email_id_seq", allocationSize = 5)
    private Long emailId;

    @Column(name = "email_from", nullable = false)
    private String emailFrom;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String emailBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EmailState state;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_id", nullable = false)
    private List<Recipient> recipients;

}
