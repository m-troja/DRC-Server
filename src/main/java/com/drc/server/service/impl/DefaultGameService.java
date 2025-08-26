package com.drc.server.service.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.entity.*;
import com.drc.server.event.GameEventPublisher;
import com.drc.server.exception.GameErrorException;
import com.drc.server.exception.GameNotFoundException;
import com.drc.server.exception.NoNextQuestionException;
import com.drc.server.exception.SetCheaterException;
import com.drc.server.persistence.GameRepo;
import com.drc.server.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultGameService implements GameService {

    private final UserService userService;
    private final RoleService roleService;
    private final AnswerService answerService;
    private final GameRepo gameRepo;
    private final GameEventPublisher gameEventPublisher;
    private final AnswerCnv answerCnv;

    public Game startNewGame() {
        Game game = new Game();
        List<User> users = userService.getUsersWithNoGame();
        if (users.isEmpty()){
            return null;
        }
        game.setCurrentQuestionId(1);
        game.setGameStatus(GameStatus.STARTED);
        game.setUsers(users);
        game.setPlayersQty(users.size());
        game.setMaxQuestion(2 * users.size() - 2);
        save(game);

        for (User user: users){
            user.setGame(game);
            userService.update(user);
        }
        setCheater(game);
        log.debug("Start new {}  ", game);
        gameEventPublisher.publishNewGameStartedEvent(game);
        return game;
    }

    public boolean allowNextQuestion(Game game) {
        Integer maxQuestion = game.getMaxQuestion();
        Integer currentQuestion = game.getCurrentQuestionId();
        if ( maxQuestion > currentQuestion) {
            log.debug("ALlowed next question. maxQuestion: {}, currentQuestion: {}", maxQuestion, currentQuestion );
            return true;
        }
        else {
            game.setGameStatus(GameStatus.END);
            save(game);
            log.debug("Reached max question! Game ended. maxQuestion: {}, currentQuestion: {}", maxQuestion, currentQuestion);
            throw new NoNextQuestionException("Reached max question! Game ended. maxQuestion:" + maxQuestion );
        }
    }

    public void setCheater(Game game) {
        List<User> allUsersInGame = userService.getUsersByGame(game);

        // Check if cheater already in game
        for (User userInGame : allUsersInGame) {
            if (userInGame.getRole().equals(roleService.getRoleByName(RoleService.ROLE_CHEATER))) {
                log.debug("Cheater already in game: {}", userInGame);
                return;
            }
        }

        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_USER), game);

        if (!users.isEmpty()) {
            Random random = new Random();
            User cheater = users.get(random.nextInt(users.size()));
            cheater.setRole(roleService.getRoleByName(RoleService.ROLE_CHEATER));
            userService.update(cheater);
            log.debug("Set cheater {} " , cheater);
        }
        else {
            log.debug("No users connected - no cheater selected");
        }
    }

    public void setCheater(String username) {
        User user = userService.getUserByname(username);
        user.setRole(roleService.getRoleByName(RoleService.ROLE_CHEATER));

        try {
            userService.update(user);
        } catch (Exception e) {
            log.debug("Error setting cheater by admin: {}" ,user);
            throw new SetCheaterException("Error setting cheater by admin for user " + user.getGame());
        }
        log.debug("Cheater set by admin: {}" ,user);
    }

    public Game getGameById(Integer id) {
        Game game = gameRepo.findById(id).orElseThrow( () -> new GameNotFoundException("GameId " + id + " was not found"));
        log.debug("Find game by id {} : {} ", id, game);
        return game;
    }

    public Game triggerNextQuestion(Game game) {
        Integer currentQuestionId = game.getCurrentQuestionId();
        Integer nextQuestionId = ++currentQuestionId;
        log.debug("currentQuestionId {}, nextQuestionId{}", currentQuestionId, nextQuestionId);
        log.debug("Game before change{}", game);
        game.setCurrentQuestionId(nextQuestionId);
        save(game);
        log.debug("Game after change{}", game);
        gameEventPublisher.publishNextQuestionEvent(game);
        return game;
    }

    public void save(Game game) {
        gameRepo.save(game);
    }

    public void deleteAllGames() {
        try {
            gameRepo.deleteAll();
            log.debug("Removed all games");
        } catch (Exception e) {
            log.debug("Error removing all games");
        }
    }

    public void sendAnswerToUsers(AnswerRequest ar) {
        Game game;
        if (getGameById(ar.gameId()) == null) {
            log.debug("Game is null, gameId = {}", ar.gameId());
            return;
        }
        else {
            game = getGameById(ar.gameId());
        }

        Answer answer;
        if (answerService.getAnswerForQuestionByValueAndGameId(ar.value(), game.getCurrentQuestionId()) == null) {
            log.debug("Answer is null, answer.value {}, gameId {}", ar.value(), game.getCurrentQuestionId());
            return;
        }
        else {
            answer = answerService.getAnswerForQuestionByValueAndGameId(ar.value(), game.getCurrentQuestionId());
        }

        AnswerDto answerDto = answerCnv.converAnswerToAnswerDto(answer);
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_USER), game);
        log.debug("sendAnswerToUsers sendAnswerToUsers: {}, {}, {}, {} ", game, answer, answerDto, users);

    }

}