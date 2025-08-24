package com.drc.server.controller;

import com.drc.server.entity.*;
import com.drc.server.exception.GameCommandNotSupportedException;
import com.drc.server.exception.GameErrorException;
import com.drc.server.exception.GameMinimumPlayerException;
import com.drc.server.service.GameService;
import com.drc.server.service.NotificationService;
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
    private final NotificationService notificationService;
    private static final String START_GAME = "START_GAME";
    private static final String NEXT_QUESTION = "NEXT_QUESTION";
    private static final String NOT_ENOUGH_PLAYERS = "Error: not enough players connected. Required: ";
    private static final String NO_PLAYERS = "Error: no users connected";
    private static final String SC_OK = "Game started {}";

    @Value("${minimum.players.qty}")
    private Integer MINIMUM_PLAYERS_QTY ;

    @GetMapping("/cmd")
    public GameStartedResponse startGame(@RequestParam("cmd") String cmd) {

        int playersConnected = webSocketSessionRegistry.getAllSessions().size() ;
        log.debug("cmd: {}", cmd);

        Game game;

        if (cmd.equals(START_GAME) ) {
            if (playersConnected >= MINIMUM_PLAYERS_QTY) {
                game = gameService.startNewGame();
                if (game == null) {
                    throw new GameErrorException("Server error! Please contact orzeu");
                }
                log.debug("Game started correctly {}", game);
                return new GameStartedResponse(ResponseType.GAME_STARTED, game.getId());
            } else {
                throw new GameMinimumPlayerException("Minimum players qty not reached. Needed: " + MINIMUM_PLAYERS_QTY + ", actual: " + playersConnected);
            }
        } else {
            throw new GameCommandNotSupportedException("Command not supported");
        }
    }

    @GetMapping("/next-question")
    public Response nextQuestion(@RequestParam("gameId") Integer gameId) {
        gameService.allowNextQuestion(gameService.getGameById(gameId));
            Question question ;
            Game game = gameService.getGameById(gameId);
            log.debug("Game before next question: {}", game);

            game = gameService.triggerNextQuestion(game);
            log.debug("Game after next question: {}", game);

            return new Response(ResponseType.NEXT_QUESTION, "Next questionId: " + game.getCurrentQuestionId());
    }

    @GetMapping("/cheater")
    public Response setCheater(@RequestParam("name") String name) {
        gameService.setCheater(name);

        return new Response(ResponseType.SET_CHEATER, "Cheater set: " + name);
    }

    @GetMapping("/kick")
    public KickRequest kickUser(@RequestParam("name") String name) {
        log.debug("Kick request for user {}", name);
        KickRequest kr = new KickRequest(RequestType.COMMAND_DISCONNECT, name);
        notificationService.sendKickRequest(kr);
        return kr;
    }
}
