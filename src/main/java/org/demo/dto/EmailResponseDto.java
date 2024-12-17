package org.demo.dto;

import lombok.Data;
import org.demo.entity.enums.EmailState;
import org.demo.entity.enums.RecipientType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailResponseDto {

    private Long emailId;
    private String emailFrom;
    private String subject;
    private String emailBody;
    private List<RecipientDto> recipients;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class RecipientDto {

        private String email;
        String type;

    }

}