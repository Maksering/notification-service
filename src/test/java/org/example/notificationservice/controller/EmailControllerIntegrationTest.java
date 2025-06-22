package org.example.notificationservice.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(0, null, "smtp"))
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);

    @BeforeEach
    void clearEmails() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

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
    }

    @Test
    void whenPost_ShouldSendEmail() throws Exception {
        String json = """
                {
                    "to": "recipient@test.com",
                    "subject": "test",
                    "messageText": "test"
                }
                """;

        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email was sent")));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        assertEquals("test", receivedMessages[0].getSubject());
        assertTrue(receivedMessages[0].getContent().toString().contains("test"));
    }

    @Test
    void whenGet_shouldSendEmail() throws Exception {
        mockMvc.perform(get("/api/email/send")
                        .param("to", "recipient@test.com")
                        .param("subject", "test")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email was sent to recipient@test.com")));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        assertEquals("test", receivedMessages[0].getSubject());
        assertTrue(receivedMessages[0].getContent().toString().contains("test"));
    }
}
