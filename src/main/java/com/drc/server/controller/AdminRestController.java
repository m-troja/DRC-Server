package com.drc.server.controller;

import com.drc.server.entity.Game;
import com.drc.server.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final String START_GAME = "START_GAME";
    private static final String NEXT_QUESTION = "NEXT_QUESTION";
    private static final Integer minimumPlayerQty = 1;
    private static final String NOT_ENOUGH_PLAYERS = "Error: not enough players connected. Required: ";
    private static final String NO_PLAYERS = "Error: no users connected";
    private static final String SC_OK = "Game started {}";

    @GetMapping("/cmd")
    public ResponseEntity<String> startGame(@RequestParam("cmd") String cmd) {

        log.debug("cmd: {}", cmd);

        Game game = new Game();

        if (cmd.equals(START_GAME))  {
            game = gameService.startNewGame();
            if( game == null) {
                log.debug("Error: game is null! Sending SC_412");
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(NO_PLAYERS);
            }
            else if (game.getUsers().size() < minimumPlayerQty) {
                log.debug("Error: not enough players connected! Sending SC_412 for {}", game);
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(NOT_ENOUGH_PLAYERS + minimumPlayerQty);
            }
            log.debug(SC_OK, game);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("GameId: " + game.getId());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Command not supported");
        }
    }

    @GetMapping("/next-question")
    public ResponseEntity<String> nextQuestion(@RequestParam("gameId") Integer gameId) {
        Game game = gameService.getGameById(gameId);
        log.debug("Game before next question: {}", game);
        game = gameService.triggerNextQuestion(game);
        log.debug("Game after next question: {}", game);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Next questionId: " + game.getCurrentQuestionId());
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
