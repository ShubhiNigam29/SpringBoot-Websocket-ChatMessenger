package com.websocket.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.websocket.chat.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class ChatController {
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage message) {
		return message;
	}
	
	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", message.getSender());
		return message;
	}
}
