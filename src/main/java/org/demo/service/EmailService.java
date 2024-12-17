package org.demo.service;

import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;
import org.demo.dto.EmailRequestDto;

public interface EmailService {

    Email saveEmail(EmailRequestDto emailRequestDto);
    Email updateEmail(Long id, Email email);
    void deleteEmail(Long id);
    void markEmailAsSpam(String email);
    void sendEmail(Long id);
    EmailResponseDto mapToEmailResponseDto(Email email);
}
