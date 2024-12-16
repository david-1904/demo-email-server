package org.demo.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.demo.entity.enums.EmailStatus;

import java.util.List;

@Data
public class EmailRequestDto {

    @NotNull(message = "Sender email cannot be null")
    private String emailFrom;

    private String emailBody;

    @NotNull(message = "Recipients cannot be null")
    private List<RecipientsDto> emailTo;

    private List<RecipientsDto> emailCC;

    @NotNull(message = "State cannot be null")
    private EmailStatus state;

    @Data
    public static class RecipientsDto {

        @NotNull
        @Email(message = "Recipient email must be valid")
        private String email;

    }

}