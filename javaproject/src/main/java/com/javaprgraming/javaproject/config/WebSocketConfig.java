package com.javaprgraming.javaproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 경로 (서버 -> 클라이언트 메시지 전송 시 사용)
        config.enableSimpleBroker("/topic");
        // 클라이언트가 메시지를 보낼 경로 (클라이언트 -> 서버 메시지 전송 시 사용)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 주소 (예: ws://localhost:8080/ws)
        // setAllowedOrigins("*") 또는 프론트엔드 주소로 설정하여 CORS 문제 방지
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}