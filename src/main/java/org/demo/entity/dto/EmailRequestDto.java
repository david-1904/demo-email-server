package org.demo.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EmailRequestDto {

    @NotNull(message = "Sender email cannot be null")
    private String emailFrom;

    private String emailBody;

    @NotNull(message = "Recipients cannot be null")
    private RecipientsDto recipients;

    @Data
    public static class RecipientsDto {

        @NotEmpty(message = "TO recipients cannot be empty")
        private List<String> to;
        private List<String> cc;
    }

}