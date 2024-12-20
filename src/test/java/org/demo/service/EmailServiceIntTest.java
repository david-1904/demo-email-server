package org.demo.service;

import org.demo.dto.EmailRequestDto;
import org.demo.repository.EmailRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class EmailServiceIntTest {
    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("email_server_test")
            .withUsername("admin")
            .withPassword("secret");

    @DynamicPropertySource
    static void configureTestContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void startContainer() {
        postgresContainer.start();
    }

    @Test
    void shouldSaveEmailsAndReturnWithIds() {
        // Arrange
        EmailRequestDto emailRequestDto1 = new EmailRequestDto();
        emailRequestDto1.setEmailFrom("sender@example.com");
        emailRequestDto1.setSubject("Subject 1");
        emailRequestDto1.setEmailBody("Body 1");

        EmailRequestDto emailRequestDto2 = new EmailRequestDto();
        emailRequestDto2.setEmailFrom("sender2@example.com");
        emailRequestDto2.setSubject("Subject 2");
        emailRequestDto2.setEmailBody("Body 2");

        EmailRequestDto.RecipientsDto recipientsDto1 = new EmailRequestDto.RecipientsDto();
        EmailRequestDto.RecipientsDto recipientsDto2 = new EmailRequestDto.RecipientsDto();
        recipientsDto1.setEmail("<EMAIL>");
        recipientsDto2.setEmail("<EMAIL>");

        List<EmailRequestDto.RecipientsDto> recipientsDtos = List.of(recipientsDto1, recipientsDto2);

        emailRequestDto1.setEmailTo(recipientsDtos);
        emailRequestDto2.setEmailTo(recipientsDtos);
        emailRequestDto1.setEmailCC(recipientsDtos);
        emailRequestDto2.setEmailCC(recipientsDtos);

        List<EmailRequestDto> emailRequestDtos = List.of(emailRequestDto1, emailRequestDto2);

        // Act
        List<Long> savedEmails = emailService.saveAllEmails(emailRequestDtos);

        // Assert
        assertEquals(2, savedEmails.size());
        assertEquals(1L, savedEmails.get(0).longValue());
        assertEquals(2L, savedEmails.get(1).longValue());
    }

}
