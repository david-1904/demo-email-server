package org.demo.service;

import org.demo.controller.exception.ResourceNotFoundException;
import org.demo.dto.EmailRequestDto;
import org.demo.dto.EmailResponseDto;
import org.demo.entity.Email;

import java.util.List;

/**
 * Service interface for managing emails.
 */
public interface EmailService {

    /**
     * Saves a new email based on the provided request DTO.
     *
     * @param emailRequestDto the DTO containing email details
     * @return the saved Email entity
     */
    Email saveEmail(EmailRequestDto emailRequestDto);

    /**
     * Updates an existing email identified by its ID.
     *
     * @param id    the ID of the email to update
     * @param email the updated email details
     * @return the updated Email entity
     * @throws ResourceNotFoundException if the email with the given ID does not exist
     * @throws IllegalStateException     if the email is not in the DRAFT state
     */
    Email updateEmail(Long id, Email email) throws ResourceNotFoundException, IllegalStateException;

    /**
     * Deletes an email by marking its state as DELETED.
     *
     * @param id the ID of the email to delete
     * @throws ResourceNotFoundException if the email with the given ID does not exist
     */
    void deleteEmail(Long id) throws ResourceNotFoundException;

    /**
     * Marks an email as SPAM based on its sender address.
     *
     * @param email the sender's email address
     * @throws ResourceNotFoundException if no email with the given sender address exists
     * @throws IllegalStateException     if the email is already marked as SPAM
     */
    void markEmailAsSpam(String email) throws ResourceNotFoundException, IllegalStateException;

    /**
     * Sends an email by updating its state to SENT.
     *
     * @param id the ID of the email to send
     * @throws ResourceNotFoundException if the email with the given ID does not exist
     * @throws IllegalStateException     if the email is not in the DRAFT state
     */
    void sendEmail(Long id) throws ResourceNotFoundException, IllegalStateException;

    /**
     * Converts an Email entity to a response DTO.
     *
     * @param email the Email entity to map
     * @return the corresponding EmailResponseDto
     */
    EmailResponseDto mapToEmailResponseDto(Email email);

    /**
     * Retrieves all emails as response DTOs.
     *
     * @return a list of EmailResponseDto
     */
    List<EmailResponseDto> getAllEmails();

    /**
     * Retrieves an email by its ID and maps it to a response DTO.
     *
     * @param id the ID of the email to retrieve
     * @return the corresponding EmailResponseDto
     * @throws ResourceNotFoundException if the email with the given ID does not exist
     */
    EmailResponseDto getEmailById(Long id) throws ResourceNotFoundException;

    /**
     * Saves multiple emails in bulk.
     *
     * @param emailRequestDtos a list of email request DTOs
     * @return a list of IDs of the saved emails
     * @throws IllegalArgumentException if the list of email request DTOs is null or empty
     */
    List<Long> saveAllEmails(List<EmailRequestDto> emailRequestDtos) throws IllegalArgumentException;

    /**
     * Deletes multiple emails by their IDs by marking their state as DELETED.
     *
     * @param ids a list of email IDs to delete
     * @throws ResourceNotFoundException if any of the provided IDs do not exist
     */
    void deleteEmailsByIds(List<Long> ids) throws ResourceNotFoundException;
}