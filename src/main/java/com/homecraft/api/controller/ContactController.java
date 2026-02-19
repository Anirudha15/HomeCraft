package com.homecraft.api.controller;

import com.homecraft.api.dto.ContactDTO;
import com.homecraft.api.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final MailService mailService;

    public ContactController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping
    public ResponseEntity<?> sendContactMessage(
            @Valid @RequestBody ContactDTO dto
    ) {
        mailService.sendContactMail(
                dto.getEmail() == null || dto.getEmail().isBlank()
                        ? "anonymous"
                        : dto.getEmail(),
                dto.getMessage()
        );

        return ResponseEntity.ok().build();
    }
}