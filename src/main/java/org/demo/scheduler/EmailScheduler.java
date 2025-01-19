package org.demo.scheduler;

import org.demo.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    private final EmailService emailService;

    public EmailScheduler(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Marks specific emails as spam.
     * Scheduled to run daily at 10:00 AM.
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void markEmailsAsSpam() {
        emailService.markEmailAsSpam("carl@my.com");
    }

}
