package com.websocket.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.ChatMessage.MessageType;


@Component
public class WebSocketEventListener {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
	
	@Autowired
	private SimpMessageSendingOperations msgTemplate;
	
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		logger.info("New web socket connection!");
	}
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String)headerAccessor.getSessionAttributes().get("username");
		if(username != null) {
			logger.info("User: " + username + "disconnected!");
			ChatMessage message = new ChatMessage();
			message.setType(MessageType.LEAVE);
			message.setSender(username);
			msgTemplate.convertAndSend("/topic/public", message);
		}
	}
}
