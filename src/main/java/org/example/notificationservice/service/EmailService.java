package org.example.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.EmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailDTO emailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(emailDTO.getTo());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getMessageText(), false);
            mailSender.send(message);
            log.info("Email was sent " + message);
        } catch (Exception e) {
            log.info("Email was not sent " + e);
            throw new RuntimeException("Fail to send Email " + e.getMessage(), e);
        }
    }
}
