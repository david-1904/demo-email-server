package org.demo.service;

import jakarta.persistence.EntityManager;
import org.demo.dto.EmailRequestDto;
import org.demo.entity.Email;
import org.demo.entity.enums.EmailState;
import org.demo.repository.EmailRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class EmailServiceIntTest {
    @Autowired
    private EmailService emailService;

    @Autowired
    private EntityManager entityManager;

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

    @Test
    void whenBatchSizeIsFive_ExpectSixBatchesForTenEntities() {
        // Arrange
        List<EmailRequestDto> emailRequestDtos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            EmailRequestDto emailRequest = new EmailRequestDto();
            emailRequest.setEmailFrom("sender" + i + "@example.com");
            emailRequest.setSubject("Subject " + i);
            emailRequest.setEmailBody("Body " + i);

            EmailRequestDto.RecipientsDto recipientTo = new EmailRequestDto.RecipientsDto();
            recipientTo.setEmail("recipient" + i + "@example.com");
            emailRequest.setEmailTo(List.of(recipientTo));
            emailRequestDtos.add(emailRequest);
        }

        // Enable Hibernate statistics
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        // Act
        emailService.saveAllEmails(emailRequestDtos);

        // Assert
        long statementCount = statistics.getPrepareStatementCount();
        System.out.println("Prepared Statement Count: " + statementCount);

        // Expect 6 Batches: 2x insert email, 2x insert recipient, 2x update recipient (email_id reference)
        assertEquals(6, statementCount, "Batching is not working as expected");
    }
}
