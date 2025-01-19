package org.demo.service;

import org.demo.controller.exception.ResourceNotFoundException;
import org.demo.dto.EmailRequestDto;
import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;
import org.demo.entity.Recipient;
import org.demo.entity.enums.EmailState;
import org.demo.entity.enums.RecipientType;
import org.demo.repository.EmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    public EmailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Email saveEmail(EmailRequestDto emailRequest) {
        Email email = mapToEmailEntity(emailRequest);
        emailRepository.save(email);
        return email;
    }

    @Override
    public Email updateEmail(Long id, Email updatedEmail) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email with ID " + id + " not found"));

        if (!EmailState.DRAFT.equals(existingEmail.getState())) {
            throw new IllegalStateException("Only DRAFT emails can be updated");
        }
        existingEmail.setSubject(updatedEmail.getSubject());
        existingEmail.setEmailBody(updatedEmail.getEmailBody());
        existingEmail.setUpdatedAt(LocalDateTime.now());

        return emailRepository.save(existingEmail);
    }

    @Override
    public void deleteEmail(Long id) {

        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email with ID " + id + " not found"));

        existingEmail.setState(EmailState.DELETED);
        emailRepository.save(existingEmail);

    }

    @Override
    public void markEmailAsSpam(String emailFrom) {
        List<Email> emails = emailRepository.findAllByEmailFrom(emailFrom);

        if (emails.isEmpty()) {
            throw new ResourceNotFoundException("No emails found from sender: " + emailFrom);
        }

        emails.stream()
                .filter(email -> !EmailState.SPAM.equals(email.getState()))
                .forEach(email -> email.setState(EmailState.SPAM));

        emailRepository.saveAll(emails);
    }

    @Override
    public void sendEmail(Long id) {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email with ID " + id + " not found"));

        if (!email.getState().equals(EmailState.DRAFT)) {
            throw new IllegalStateException("Only DRAFT emails can be sent.");
        }

        email.setState(EmailState.SENT);
        email.setUpdatedAt(LocalDateTime.now());
        emailRepository.save(email);
    }

    private Email mapToEmailEntity(EmailRequestDto emailRequest) {
        Email email = Email.builder()
                .emailFrom(emailRequest.getEmailFrom())
                .emailBody(emailRequest.getEmailBody())
                .subject(emailRequest.getSubject())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .state(EmailState.DRAFT)
                .build();

        List<Recipient> recipientsTo = mapToRecipientEntity(emailRequest.getEmailTo(), email, RecipientType.TO);
        List<Recipient> recipients = new ArrayList<>(recipientsTo);

        if (!emailRequest.getEmailCC().isEmpty()) {
            List<Recipient> recipientsCC = mapToRecipientEntity(emailRequest.getEmailCC(), email, RecipientType.CC);
            recipients.addAll(recipientsCC);
        }

        email.setRecipients(recipients);

        return email;
    }

    private List<Recipient> mapToRecipientEntity(List<EmailRequestDto.RecipientsDto> recipientsDtos,
                                                 Email email, RecipientType type) {
        return recipientsDtos.stream()
                .map(recipientDto -> {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(recipientDto.getEmail());
                    recipient.setType(type);
                    recipient.setEmailReference(email); // Setze die Referenz zur Email-Entität
                    return recipient;
                })
                .collect(Collectors.toList());
    }

    public EmailResponseDto mapToEmailResponseDto(Email email) {
        List<EmailResponseDto.RecipientDto> recipientDtos = email.getRecipients().stream()
                .map(recipient -> EmailResponseDto.RecipientDto.builder()
                        .email(recipient.getEmail())
                        .type(recipient.getType().name())
                        .build())
                .collect(Collectors.toList());

        return EmailResponseDto.builder()
                .emailId(email.getEmailId())
                .emailFrom(email.getEmailFrom())
                .subject(email.getSubject())
                .emailBody(email.getEmailBody())
                .state(email.getState().name())
                .createdAt(email.getCreatedAt())
                .updatedAt(email.getUpdatedAt())
                .recipients(recipientDtos)
                .build();
    }

    @Override
    public List<EmailResponseDto> getAllEmails() {
        return emailRepository.findAll().stream()
                .map(this::mapToEmailResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmailResponseDto getEmailById(Long id) {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email with ID " + id + " not found"));
        return mapToEmailResponseDto(email);
    }

    @Transactional
    @Override
    public List<Long> saveAllEmails(List<EmailRequestDto> emailRequestDtos) {
        if (emailRequestDtos == null || emailRequestDtos.isEmpty()) {
            throw new IllegalArgumentException("Email list cannot be null or empty");
        }
        List<Email> emails = emailRequestDtos.stream()
                .map(this::mapToEmailEntity)
                .collect(Collectors.toList());

        emailRepository.saveAll(emails);
        return emails.stream().map(Email::getEmailId).toList();
    }

    @Transactional
    @Override
    public void deleteEmailsByIds(List<Long> ids) {
        List<Email> emails = emailRepository.findAllById(ids);

        Set<Long> foundIds = emails.stream()
                .map(Email::getEmailId)
                .collect(Collectors.toSet());

        List<Long> missingIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Emails with IDs " + missingIds + " not found");
        }

        emails.forEach(email -> email.setState(EmailState.DELETED));

        emailRepository.saveAll(emails);
    }

}