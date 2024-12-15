package org.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;

    private String emailFrom;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private List<Recipient> emailTo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private List<Recipient> emailCC;

    private String emailBody;

    private Integer state;

    private Timestamp createdAt;
    private Timestamp updatedAt;

}
