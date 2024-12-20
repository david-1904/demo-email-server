package org.demo.controller;

import org.demo.dto.EmailRequestDto;
import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;
import org.demo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailResponseDto> getAllEmails(@PathVariable Long id) {
        return ResponseEntity.ok(emailService.getEmailById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmailResponseDto>> getEmail() {
        return ResponseEntity.ok(emailService.getAllEmails());
    }

    @PostMapping
    public ResponseEntity<EmailResponseDto> createEmail(@RequestBody EmailRequestDto emailRequestDto) {
        Email email = emailService.saveEmail(emailRequestDto);
        EmailResponseDto responseDto = emailService.mapToEmailResponseDto(email);
        URI location = URI.create("/emails/" + email.getEmailId());
        return ResponseEntity.created(location).body(responseDto);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Long>> createEmails(@RequestBody List<EmailRequestDto> emailRequestDtos) {
        List<Long> savedIds = emailService.saveAllEmails(emailRequestDtos);
        return ResponseEntity.ok(savedIds);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailResponseDto> updateEmail(@PathVariable Long id,
                                             @RequestBody Email email) {
        Email updatedEmail = emailService.updateEmail(id, email);
        EmailResponseDto responseDto = emailService.mapToEmailResponseDto(updatedEmail);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}/send")
    public ResponseEntity<String> sendEmail(@PathVariable Long id) {
        emailService.sendEmail(id);
        return ResponseEntity.ok("Email sent successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        emailService.deleteEmail(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteEmails(@RequestParam List<Long> ids) {
        emailService.deleteEmailsByIds(ids);
        return ResponseEntity.noContent().build();
    }

}
