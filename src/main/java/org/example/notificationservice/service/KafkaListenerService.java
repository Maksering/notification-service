package org.example.notificationservice.service;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.notificationservice.dto.EmailDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KafkaListenerService {

    private static final String CREATE_OPERATION = "create";
    private static final String DELETE_OPERATION = "delete";

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "user-events-listeners")
    private void handleUserEvent(ConsumerRecord<String, String> record) {
        switch (record.key()) {
            case CREATE_OPERATION:
                handleCreateUser(record.value());
                break;
            case DELETE_OPERATION:
                handleDeleteUser(record.value());
                break;
            default:
                System.out.println("Unknown operation" + record.key());
        }
    }

    private void handleCreateUser(String value) {
        emailService.sendEmail(new EmailDTO(value, CREATE_OPERATION,
                "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."));
    }

    private void handleDeleteUser(String value) {
        emailService.sendEmail(new EmailDTO(value, DELETE_OPERATION,
                "Здравствуйте! Ваш аккаунт был удалён."));
    }
}
