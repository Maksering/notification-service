package org.example.notificationservice.controller;

import lombok.AllArgsConstructor;
import org.example.notificationservice.dto.EmailDTO;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    private ResponseEntity<String> sendEmail(@RequestBody EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO);
        return ResponseEntity.ok("Email was sent");
    }

    //for browser send
    @GetMapping("/send")
    public ResponseEntity<String> sendEmailViaBrowser(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text) {
        EmailDTO emailDTO = new EmailDTO(to, subject, text);
        emailService.sendEmail(emailDTO);
        return ResponseEntity.ok("Email was sent to " + to);
    }
}
