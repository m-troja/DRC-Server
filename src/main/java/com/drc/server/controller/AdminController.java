package com.drc.server.controller;

import com.drc.server.entity.Game;
import com.drc.server.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/admin")
@RestController
@Slf4j
public class AdminController {

    private GameService gameService;

    private final String START_GAME = "START_GAME";
    private final String NEXT_QUESTION = "NEXT_QUESTION";
    private final Integer minimumPlayerQty = 1;
    private final String NOT_ENOUGH_PLAYERS = "Error: not enough players connected. Required: ";
    private final String NO_PLAYERS = "Error: no users connected";
    private final String SC_OK = "Game started {}";

    @GetMapping("/cmd")
    public ResponseEntity<String> startGame(@RequestParam("cmd") String cmd) {

        log.debug("cmd: {}", cmd);

        Game game = new Game();

        if (cmd.equals(START_GAME))  {
            game = gameService.startNewGame();
            if( game == null) {
                log.debug("Error: game is null! Sending SC_412 for {}", game);
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(NO_PLAYERS);
            }
            else if (game.getUsers().size() < minimumPlayerQty) {
                log.debug("Error: not enough players connected! Sending SC_412 for {}", game);
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(NOT_ENOUGH_PLAYERS + minimumPlayerQty);
            }
            log.debug(SC_OK, game);
            gameService.sendQuestionToAllClients(game);
            gameService.sendAnswers(game);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("GameId: " + game.getId());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Command not supported");
        }
    }

    @GetMapping("/next-question")
    public ResponseEntity<String> nextQuestion(@RequestParam("gameId") String gameId) {
        Game game = gameService.getGameById(Integer.valueOf(gameId));
        log.debug("Game before next question: {}", game);
        game = gameService.triggerNextQuestion(game);
        log.debug("Game after next question: {}", game);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Next questionId: " + game.getCurrentQuestionId());
    }

    public AdminController(GameService gameService) {
        this.gameService = gameService;
    }
}
