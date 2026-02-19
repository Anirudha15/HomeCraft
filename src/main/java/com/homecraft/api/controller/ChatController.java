package com.homecraft.api.controller;

import com.homecraft.api.dto.ChatRequestDTO;
import com.homecraft.api.dto.ChatResponseDTO;
import com.homecraft.api.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public ChatResponseDTO chat(
            Authentication auth,
            @RequestBody ChatRequestDTO dto
    ) {
        Integer userId = Integer.parseInt(auth.getName());
        return new ChatResponseDTO(
                chatService.reply(userId, dto.getMessage())
        );
    }

    @GetMapping("/api/test/gemini")
    public String testGemini(ChatService chatService) {
        return chatService.reply(1, "Say hello in one sentence");
    }
}