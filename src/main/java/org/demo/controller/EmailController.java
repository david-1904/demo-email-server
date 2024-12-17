package org.demo.controller;

import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;
import org.demo.dto.EmailRequestDto;
import org.demo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EmailResponseDto> createEmail(@RequestBody EmailRequestDto email) {
        Email createdEmail = emailService.saveEmail(email);
        EmailResponseDto responseDto = emailService.mapToEmailResponseDto(createdEmail);
        URI location = URI.create("/emails/" + createdEmail.getEmailId());
        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailResponseDto> updateEmail(@PathVariable Long id,
                                             @RequestBody Email email) {
        Email updatedEmail = emailService.updateEmail(id, email);
        EmailResponseDto responseDto = emailService.mapToEmailResponseDto(updatedEmail);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        emailService.deleteEmail(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/send")
    public ResponseEntity<String> sendEmail(@PathVariable Long id) {
        emailService.sendEmail(id);
        return ResponseEntity.ok("Email sent successfully!");
    }
    
}
