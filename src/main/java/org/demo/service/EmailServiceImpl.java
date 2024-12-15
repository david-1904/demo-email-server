package org.demo.service;

import org.demo.entity.Email;
import org.demo.entity.EmailStatus;
import org.demo.entity.Recipient;
import org.demo.entity.RecipientType;
import org.demo.entity.dto.EmailRequestDto;
import org.demo.repository.EmailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    public EmailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Email saveEmail(EmailRequestDto emailRequest) {
        Email email = new Email();
        email.setEmailFrom(emailRequest.getEmailFrom());
        email.setEmailBody(emailRequest.getEmailBody());
        email.setState(EmailStatus.DRAFT);
        email.setCreatedAt(LocalDateTime.now());
        email.setUpdatedAt(LocalDateTime.now());

        List<Recipient> recipients = new ArrayList<>();

        for (String to : emailRequest.getRecipients().getTo()) {
            Recipient recipient = new Recipient();
            recipient.setEmail(to);
            recipient.setType(RecipientType.TO);
            recipients.add(recipient);
        }

        for (String cc : emailRequest.getRecipients().getCc()) {
            Recipient recipient = new Recipient();
            recipient.setEmail(cc);
            recipient.setType(RecipientType.CC);
            recipients.add(recipient);
        }
        email.setRecipients(recipients);

        emailRepository.save(email);
        return email;
    }

    @Override
    public Email updateEmail(Long id, Email email) {
        return null;
    }

}