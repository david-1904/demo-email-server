package org.demo.service;

import org.demo.controller.exception.ResourceNotFoundException;
import org.demo.dto.EmailRequestDto;
import org.demo.entity.Email;
import org.demo.entity.Recipient;
import org.demo.entity.enums.EmailState;
import org.demo.entity.enums.RecipientType;
import org.demo.repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void  whenGetEmails_Expect_GetAllEmails() {
        // Arrange
        Email email = new Email();
        email.setSubject("New Subject");
        email.setEmailBody("New Body");
        email.setState(EmailState.DRAFT);

        Recipient recipient = new Recipient();
        recipient.setType(RecipientType.CC);
        email.setRecipients(List.of(recipient));

        when(emailRepository.findAll()).thenReturn(List.of(email));

        // Act
        emailService.getAllEmails();

        // Assert
        verify(emailRepository).findAll();
        assertEquals(1, emailService.getAllEmails().size());
        assertEquals("New Subject", emailService.getAllEmails().get(0).getSubject());
        assertEquals("New Body", emailService.getAllEmails().get(0).getEmailBody());
    }

    @Test
    void whenSaveEmail_Expect_SaveAndReturnEmail() {
        // Arrange
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmailFrom("test@example.com");
        emailRequest.setSubject("Test Subject");
        emailRequest.setEmailBody("Test Body");
        emailRequest.setEmailTo(List.of(new EmailRequestDto.RecipientsDto()));
        emailRequest.setEmailCC(List.of());

        Email savedEmail = new Email();
        savedEmail.setEmailId(1L);
        savedEmail.setEmailFrom("test@example.com");
        savedEmail.setSubject("Test Subject");
        savedEmail.setEmailBody("Test Body");
        savedEmail.setState(EmailState.DRAFT);

        when(emailRepository.save(any(Email.class))).thenReturn(savedEmail);

        // Act
        Email result = emailService.saveEmail(emailRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmailFrom());
        assertEquals("Test Subject", result.getSubject());
        verify(emailRepository).save(any(Email.class));
    }

    @Test
    void whenUpdateEmail_Expect_UpdateEmail_WhenInDraftState() {
        // Arrange
        Long emailId = 1L;
        Email existingEmail = new Email();
        existingEmail.setEmailId(emailId);
        existingEmail.setEmailFrom("test@example.com");
        existingEmail.setSubject("Old Subject");
        existingEmail.setEmailBody("Old Body");
        existingEmail.setState(EmailState.DRAFT);

        Email updatedEmail = new Email();
        updatedEmail.setSubject("New Subject");
        updatedEmail.setEmailBody("New Body");

        when(emailRepository.findById(emailId)).thenReturn(Optional.of(existingEmail));
        when(emailRepository.save(any(Email.class))).thenReturn(existingEmail);

        // Act
        Email result = emailService.updateEmail(emailId, updatedEmail);

        // Assert
        assertEquals("New Subject", result.getSubject());
        assertEquals("New Body", result.getEmailBody());
        verify(emailRepository).save(existingEmail);
    }

    @Test
    void whenSendEmail_Expect_ChangeStateToSent() {
        // Arrange
        Long emailId = 1L;
        Email email = new Email();
        email.setEmailId(emailId);
        email.setState(EmailState.DRAFT);

        when(emailRepository.findById(emailId)).thenReturn(Optional.of(email));

        // Act
        emailService.sendEmail(emailId);

        // Assert
        assertEquals(EmailState.SENT, email.getState());
        verify(emailRepository).save(email);
    }

    @Test
    void whenDeleteEmail_Expect_MarkEmailAsDeleted() {
        // Arrange
        Long emailId = 1L;
        Email email = new Email();
        email.setEmailId(emailId);
        email.setState(EmailState.DRAFT);

        when(emailRepository.findById(emailId)).thenReturn(Optional.of(email));

        // Act
        emailService.deleteEmail(emailId);

        // Assert
        assertEquals(EmailState.DELETED, email.getState());
        verify(emailRepository).save(email);
    }

    @Test
    void whenDeleteEmailsByIds_Expect_MarkAllEmailsAsDeleted() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        Email email1 = new Email();
        email1.setEmailId(1L);
        email1.setState(EmailState.DRAFT);

        Email email2 = new Email();
        email2.setEmailId(2L);
        email2.setState(EmailState.DRAFT);

        when(emailRepository.findAllById(ids)).thenReturn(List.of(email1, email2));

        // Act
        emailService.deleteEmailsByIds(ids);

        // Assert
        assertEquals(EmailState.DELETED, email1.getState());
        assertEquals(EmailState.DELETED, email2.getState());
        verify(emailRepository).saveAll(anyList());
    }

    @Test
    void whenMarkEmailAsSpam_Expect_ChangeStateToSpam() {
        // Arrange
        String emailFrom = "test@example.com";

        Email email = new Email();
        email.setEmailId(1L);
        email.setEmailFrom(emailFrom);
        email.setState(EmailState.DRAFT);

        when(emailRepository.findByEmailFrom(emailFrom)).thenReturn(Optional.of(email));

        // Act
        emailService.markEmailAsSpam(emailFrom);

        // Assert
        assertEquals(EmailState.SPAM, email.getState());

    }

    @Test
    void whenMarkEmailAsSpam_Expect_ThrowException_WhenAlreadySpam() {
        // Arrange
        String emailFrom = "test@example.com";

        Email email = new Email();
        email.setEmailId(1L);
        email.setEmailFrom(emailFrom);
        email.setState(EmailState.SPAM);

        when(emailRepository.findByEmailFrom(emailFrom)).thenReturn(Optional.of(email));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> emailService.markEmailAsSpam(emailFrom));

        verify(emailRepository, never()).save(any());
    }

    @Test
    void whenMarkEmailAsSpam_Expect_ThrowException_WhenEmailNotFound() {
        // Arrange
        String emailFrom = "nonexistent@example.com";

        when(emailRepository.findByEmailFrom(emailFrom)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> emailService.markEmailAsSpam(emailFrom));
        verify(emailRepository, never()).save(any());
    }

    @Test
    void whenSaveAllEmails_Expect_SaveAllEmails() {
        // Arrange
        EmailRequestDto emailRequestDto1 = new EmailRequestDto();
        emailRequestDto1.setEmailFrom("sender1@example.com");
        emailRequestDto1.setSubject("Subject 1");
        emailRequestDto1.setEmailBody("Body 1");
        emailRequestDto1.setEmailTo(List.of(new EmailRequestDto.RecipientsDto()));
        emailRequestDto1.setEmailCC(List.of(new EmailRequestDto.RecipientsDto()));

        EmailRequestDto emailRequestDto2 = new EmailRequestDto();
        emailRequestDto2.setEmailFrom("sender2@example.com");
        emailRequestDto2.setSubject("Subject 2");
        emailRequestDto2.setEmailBody("Body 2");
        emailRequestDto2.setEmailTo(List.of(new EmailRequestDto.RecipientsDto()));
        emailRequestDto2.setEmailCC(List.of(new EmailRequestDto.RecipientsDto()));

        List<EmailRequestDto> requestDtos = List.of(emailRequestDto1, emailRequestDto2);

        Email email1 = new Email();
        email1.setEmailId(1L);
        email1.setEmailFrom(emailRequestDto1.getEmailFrom());
        email1.setSubject(emailRequestDto1.getSubject());
        email1.setEmailBody(emailRequestDto1.getEmailBody());

        Email email2 = new Email();
        email2.setEmailId(2L);
        email2.setEmailFrom(emailRequestDto2.getEmailFrom());
        email2.setSubject(emailRequestDto2.getSubject());
        email2.setEmailBody(emailRequestDto2.getEmailBody());

        // Mock saveAll method to simulate invocation
        when(emailRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Email> emails = invocation.getArgument(0);
            emails.get(0).setEmailId(1L);
            emails.get(1).setEmailId(2L);
            return emails;
        });

        // Act
        List<Long> savedIds = emailService.saveAllEmails(requestDtos);

        // Assert
        assertEquals(List.of(1L, 2L), savedIds);

        verify(emailRepository).saveAll(anyList());

        // Capture the email list argument
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(emailRepository).saveAll(captor.capture());
        List<Email> emailArgument = captor.getValue();

        assertEquals(2, emailArgument.size());
        assertEquals("sender1@example.com", emailArgument.get(0).getEmailFrom());
        assertEquals("sender2@example.com", emailArgument.get(1).getEmailFrom());
    }

}
