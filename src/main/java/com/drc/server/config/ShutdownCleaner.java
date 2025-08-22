package com.drc.server.config;

import com.drc.server.service.GameService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ShutdownCleaner {

    private final GameService gameService;

    @PreDestroy
    public void onShutdown() {
        gameService.deleteAllGames();
    }
}
