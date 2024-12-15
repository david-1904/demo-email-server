package org.demo.controller;

import org.demo.entity.Email;
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
    public ResponseEntity<Email> createEmail(@RequestBody Email email) {
        return ResponseEntity.ok(emailService.createEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Email> updateEmail(@PathVariable Long id, 
                                             @RequestBody Email updatedEmail) {
        return ResponseEntity.ok(emailService.updateEmail(id, updatedEmail));
    }
    
}
