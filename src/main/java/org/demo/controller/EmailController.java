package org.demo.controller;

import org.demo.entity.Email;
import org.demo.entity.dto.EmailRequestDto;
import org.demo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> createEmail(@RequestBody EmailRequestDto emailRequest) {
        emailService.saveEmail(emailRequest);
        return ResponseEntity.ok("Email created successfully!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Email> updateEmail(@PathVariable Long id, 
                                             @RequestBody Email updatedEmail) {
        return ResponseEntity.ok(emailService.updateEmail(id, updatedEmail));
    }
    
}
