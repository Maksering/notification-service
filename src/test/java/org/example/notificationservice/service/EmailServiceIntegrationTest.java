package org.example.notificationservice.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.example.notificationservice.dto.EmailDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
                "spring.mail.test-connection=false"
        }
)
public class EmailServiceIntegrationTest {
    @Autowired
    private EmailService emailService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(0, null, "smtp"))
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);

    @DynamicPropertySource
    static void configureMailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.username", () -> "test@test.com");
        registry.add("spring.mail.password", () -> "test");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "true");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.connectiontimeout", () -> "1000");
        registry.add("spring.mail.properties.mail.smtp.timeout", () -> "1000");
        registry.add("spring.mail.properties.mail.smtp.writetimeout", () -> "1000");
    }

    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo("recipient@test.com");
        emailDTO.setSubject("Test");
        emailDTO.setMessageText("Test");

        emailService.sendEmail(emailDTO);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];
        assertEquals(emailDTO.getSubject(), receivedMessage.getSubject());
        assertEquals(emailDTO.getTo(), receivedMessage.getAllRecipients()[0].toString());
        assertTrue(receivedMessage.getContent().toString().contains(emailDTO.getMessageText()));
    }
}