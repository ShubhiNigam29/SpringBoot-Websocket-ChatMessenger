package com.websocket.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override
	// Register a websocket endpoint that the clients will use to connect to the websocket server
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// SockJS is used to enable fallback options for browsers that don’t support websocket
		registry.addEndpoint("/ws").withSockJS();
	}
	
	@Override
	// Configure a message broker that will be used to route messages from one client to another
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		// In-memory message broker broadcasts messages to all the connected clients who are subscribed to a particular topic
		registry.enableSimpleBroker("/topic");
	}
}
