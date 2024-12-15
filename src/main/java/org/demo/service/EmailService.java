package org.demo.service;

import org.demo.entity.Email;
import org.demo.entity.dto.EmailRequestDto;

public interface EmailService {
    Email saveEmail(EmailRequestDto emailRequestDto);
    Email updateEmail(Long id, Email email);
}
