package org.demo.service;

import org.demo.controller.exception.ResourceNotFoundException;
import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;
import org.demo.entity.Recipient;
import org.demo.entity.enums.EmailState;
import org.demo.entity.enums.RecipientType;
import org.demo.dto.EmailRequestDto;
import org.demo.repository.EmailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        email.setState(EmailState.DRAFT);
        emailRepository.save(email);
        return email;
    }

    @Override
    public Email updateEmail(Long id, Email updatedEmail) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email with ID " + id + " not found"));

        // Check if status is DRAFT
        if (!EmailState.DRAFT.equals(existingEmail.getState())) {
            throw new IllegalStateException("Only DRAFT emails can be updated");
        }

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

        Email existingEmail = emailRepository.findByEmailFrom(emailFrom)
                .orElseThrow(() -> new ResourceNotFoundException("Email '" + emailFrom + "' not found"));

        if(EmailState.SPAM.equals(existingEmail.getState())) {
            throw new IllegalStateException("Already marked as spam email");
        }

        existingEmail.setState(EmailState.SPAM);

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
        Email email = new Email();
        email.setEmailFrom(emailRequest.getEmailFrom());
        email.setEmailBody(emailRequest.getEmailBody());
        email.setSubject(emailRequest.getSubject());
        email.setCreatedAt(LocalDateTime.now());
        email.setUpdatedAt(LocalDateTime.now());

        List<Recipient> recipientsTo = mapToRecipientEntity(emailRequest.getEmailTo(), email, RecipientType.TO);

        List<Recipient> recipientsCC = mapToRecipientEntity(emailRequest.getEmailCC(), email, RecipientType.CC);

        List<Recipient> allRecipients = new ArrayList<>();
        allRecipients.addAll(recipientsTo);
        allRecipients.addAll(recipientsCC);

        email.setRecipients(allRecipients);
        return email;
    }

    private List<Recipient> mapToRecipientEntity(List<EmailRequestDto.RecipientsDto> recipientsDtos,
                                                 Email email, RecipientType type) {
        return recipientsDtos.stream()
                .map(recipientDto -> {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(recipientDto.getEmail());
                    recipient.setType(type);
                    recipient.setEmailReference(email);
                    return recipient;
                })
                .collect(Collectors.toList());
    }

    public EmailResponseDto mapToEmailResponseDto(Email email) {
        EmailResponseDto responseDto = new EmailResponseDto();
        responseDto.setEmailId(email.getEmailId());
        responseDto.setEmailFrom(email.getEmailFrom());
        responseDto.setSubject(email.getSubject());
        responseDto.setEmailBody(email.getEmailBody());
        responseDto.setState(email.getState().name());
        responseDto.setUpdatedAt(email.getUpdatedAt());
        responseDto.setCreatedAt(email.getCreatedAt());

        List<EmailResponseDto.RecipientDto> recipientDtos = email.getRecipients().stream()
                .map(recipient -> {
                    EmailResponseDto.RecipientDto recipientDto = new EmailResponseDto.RecipientDto();
                    recipientDto.setEmail(recipient.getEmail());
                    recipientDto.setType(recipient.getType().name());
                    return recipientDto;
                })
                .collect(Collectors.toList());
        responseDto.setRecipients(recipientDtos);

        return responseDto;
    }

}