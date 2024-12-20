package org.demo.service;

import org.demo.dto.EmailRequestDto;
import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;

import java.util.List;

public interface EmailService {

    Email saveEmail(EmailRequestDto emailRequestDto);
    Email updateEmail(Long id, Email email);
    void deleteEmail(Long id);
    void markEmailAsSpam(String email);
    void sendEmail(Long id);
    EmailResponseDto mapToEmailResponseDto(Email email);
    List<EmailResponseDto> getAllEmails();
    EmailResponseDto getEmailById(Long id);
    List<Long> saveAllEmails(List<EmailRequestDto> emailRequestDtos);
    void deleteEmailsByIds(List<Long> ids);
}
