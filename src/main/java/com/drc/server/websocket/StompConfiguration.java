package com.drc.server.websocket;

import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
 * STOMP Configuration
 */
@AllArgsConstructor
@EnableScheduling
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    private final UserService userService;
    private final WebSocketSessionRegistry sessionRegistry;
    private final WebSocketHandshakeInterceptor handshakeInterceptor;
    private final UserPrincipalChannelInterceptor userPrincipalChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/client","/queue","/user");
        config.setApplicationDestinationPrefixes("/server");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/game")
                .setAllowedOrigins("http://localhost", "http://localhost:3000")
                .setHandshakeHandler(new UserHandshakeHandler())
                .addInterceptors(handshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(userPrincipalChannelInterceptor);
        log.debug("Registered UserPrincipalChannelInterceptor");
    }

}