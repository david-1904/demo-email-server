package org.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
    @Builder
    public static class RecipientDto {

        private String email;
        String type;

    }

}