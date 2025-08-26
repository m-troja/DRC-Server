package com.drc.server.websocket;

import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/*
 * STOMP Configuration
 */
@AllArgsConstructor
@EnableScheduling
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    @Autowired
    WebSocketHandshakeInterceptor handshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/client","/queue","/user");
        config.enableSimpleBroker("/client","/user");
        config.setApplicationDestinationPrefixes("/server");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/game")
//                .allowedOriginPatterns("http://192.168.*.*")
//                .setAllowedOrigins("http://localhost", "http://localhost:3000")
                .setAllowedOriginPatterns("*")
                .addInterceptors(handshakeInterceptor)
                .withSockJS();
    }
}