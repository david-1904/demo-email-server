package org.demo.service;

import org.demo.entity.Email;
import org.demo.entity.enums.EmailStatus;
import org.demo.entity.Recipient;
import org.demo.entity.enums.RecipientType;
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
        email.setState(emailRequest.getState());
        email.setCreatedAt(LocalDateTime.now());
        email.setUpdatedAt(LocalDateTime.now());

        List<Recipient> recipients = new ArrayList<>();

        for (EmailRequestDto.RecipientsDto recipientDto : emailRequest.getEmailTo()) {
            Recipient recipient = new Recipient();
            recipient.setEmail(recipientDto.getEmail());
            recipient.setEmailReference(email);
            recipient.setType(RecipientType.TO);
            recipients.add(recipient);
        }

        if (emailRequest.getEmailCC() != null) { // Optional
            for (EmailRequestDto.RecipientsDto recipientDto : emailRequest.getEmailCC()) {
                Recipient recipient = new Recipient();
                recipient.setEmail(recipientDto.getEmail());
                recipient.setEmailReference(email);
                recipient.setType(RecipientType.CC);
                recipients.add(recipient);
            }
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