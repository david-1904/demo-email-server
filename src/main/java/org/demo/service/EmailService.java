package org.demo.service;

import org.demo.entity.Email;

public interface EmailService {
    Email createEmail(Email email);
    Email updateEmail(Long id, Email email);
}
