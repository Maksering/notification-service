package org.example.notificationservice.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.notificationservice.dto.EmailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableKafka
@EmbeddedKafka(partitions = 1, topics = { "user-events" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaListenerServiceTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmailService emailService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }
    }

    @BeforeEach
    public void resetMocks() {
        reset(emailService);
    }

    @Test
    public void whenCreateUserEventSent_thenSendEmailCalledWithCreate() throws Exception {
        String email = "user@example.com";
        String key = "create";

        kafkaTemplate.send(new ProducerRecord<>("user-events", key, email)).get();

        Thread.sleep(2000);

        ArgumentCaptor<EmailDTO> captor = ArgumentCaptor.forClass(EmailDTO.class);
        Mockito.verify(emailService, times(1)).sendEmail(captor.capture());

        EmailDTO sentEmail = captor.getValue();
        assertEquals(email, sentEmail.getTo());
        assertEquals("create", sentEmail.getSubject());
        assertTrue(sentEmail.getMessageText().contains("создан"));
    }

    @Test
    public void whenDeleteUserEventSent_thenSendEmailCalledWithDelete() throws Exception {
        String email = "user@example.com";
        String key = "delete";

        kafkaTemplate.send(new ProducerRecord<>("user-events", key, email)).get();

        Thread.sleep(2000);

        ArgumentCaptor<EmailDTO> captor = ArgumentCaptor.forClass(EmailDTO.class);
        Mockito.verify(emailService, times(1)).sendEmail(captor.capture());

        EmailDTO sentEmail = captor.getValue();
        assertEquals(email, sentEmail.getTo());
        assertEquals("delete", sentEmail.getSubject());
        assertTrue(sentEmail.getMessageText().contains("удалён"));
    }
}
