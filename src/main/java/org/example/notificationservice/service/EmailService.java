package org.example.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.notificationservice.dto.EmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
        } catch (Exception e) {
            throw new RuntimeException("Fail to send Email " + e.getMessage(), e);
        }
    }
}
