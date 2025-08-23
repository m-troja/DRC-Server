package com.drc.server.controller;

import com.drc.server.entity.Game;
import com.drc.server.service.GameService;
import com.drc.server.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/admin")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminRestController {

    private final GameService gameService;
    private final WebSocketSessionRegistry webSocketSessionRegistry;

    private static final String START_GAME = "START_GAME";
    private static final String NEXT_QUESTION = "NEXT_QUESTION";
    private static final String NOT_ENOUGH_PLAYERS = "Error: not enough players connected. Required: ";
    private static final String NO_PLAYERS = "Error: no users connected";
    private static final String SC_OK = "Game started {}";

    @Value("${minimum.players.qty}")
    private Integer MINIMUM_PLAYERS_QTY ;

    @GetMapping("/cmd")
    public ResponseEntity<String> startGame(@RequestParam("cmd") String cmd) {

        int playersConnected = webSocketSessionRegistry.getAllSessions().size() ;
        log.debug("cmd: {}", cmd);

        Game game;

        if (cmd.equals(START_GAME) ) {
            if (playersConnected >= MINIMUM_PLAYERS_QTY) {
                game = gameService.startNewGame();
                if (game == null) {
                    log.debug("Server error! Please contact orzeu");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(NO_PLAYERS);
                }
                log.debug("Game started correctly {}", game);
                return ResponseEntity.status(HttpStatus.OK)
                        .body("GameId: " + game.getId());
            }
            else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Minimum players qty not reached. Needed: " + MINIMUM_PLAYERS_QTY + ", actual: " + playersConnected);
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Command not supported");
        }
    }

    @GetMapping("/next-question")
    public ResponseEntity<String> nextQuestion(@RequestParam("gameId") Integer gameId) {
        if (gameService.allowNextQuestion(gameService.getGameById(gameId))) {
            Game game = gameService.getGameById(gameId);
            log.debug("Game before next question: {}", game);
            game = gameService.triggerNextQuestion(game);
            log.debug("Game after next question: {}", game);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Next questionId: " + game.getCurrentQuestionId());
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error requesting new question. You ran out of questions! Game ended.");
        }
    }

    @GetMapping("/cheater")
    public ResponseEntity<String> setCheater(@RequestParam("name") String name) {
        try {
            gameService.setCheater(name);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error setting cheater for user: " + name);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Cheater set: " + name);
    }
}
