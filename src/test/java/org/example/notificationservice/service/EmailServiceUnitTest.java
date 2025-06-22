package org.example.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import org.example.notificationservice.dto.EmailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class EmailServiceUnitTest {
    @Test
    void shouldThrowExceptionWhenEmailSendingFails() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));
        doThrow(new RuntimeException("SMTP failure")).when(mailSender).send(any(MimeMessage.class));

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo("recipient@test.com");
        emailDTO.setSubject("Test");
        emailDTO.setMessageText("Test");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.sendEmail(emailDTO));

        assertTrue(exception.getMessage().contains("Fail to send Email"));
    }
}
