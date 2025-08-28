package com.drc.server.service.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.CorrectAnswerResponseDto;
import com.drc.server.dto.UserDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.UserCnv;
import com.drc.server.entity.*;
import com.drc.server.event.GameEventPublisher;
import com.drc.server.exception.GameNotFoundException;
import com.drc.server.exception.NoNextQuestionException;
import com.drc.server.exception.SetCheaterException;
import com.drc.server.exception.UserNotFoundException;
import com.drc.server.persistence.GameRepo;
import com.drc.server.service.*;
import com.drc.server.service.notification.AdminNotificationService;
import com.drc.server.service.notification.CheaterNotificationService;
import com.drc.server.service.notification.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.drc.server.service.RoleService.ROLE_ADMIN;
import static com.drc.server.service.RoleService.ROLE_USER;
import static com.drc.server.service.RoleService.ROLE_CHEATER;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultGameService implements GameService {

    private final UserService userService;
    private final RoleService roleService;
    private final AnswerService answerService;
    @Lazy
    private final BalanceService balanceService;
    private final GameRepo gameRepo;
    private final GameEventPublisher gameEventPublisher;
    private final AnswerCnv answerCnv;
    private final UserCnv userCnv;
    private final UserNotificationService userNotificationService;
    private final AdminNotificationService adminNotificationService;
    private final CheaterNotificationService cheaterNotificationService;


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
        String cheater = setCheaterByServer(game.getId());
        log.debug("Start new {}", game);
        gameEventPublisher.publishNewGameStartedEvent(game);
        return game;
    }

    public boolean allowNextQuestion(Game game) {
        Integer maxQuestion = game.getMaxQuestion();
        Integer currentQuestion = game.getCurrentQuestionId();
        if ( maxQuestion > currentQuestion) {
            log.debug("Allowed next question. maxQuestion: {}, currentQuestion: {}", maxQuestion, currentQuestion );
            return true;
        }
        else {
            game.setGameStatus(GameStatus.END);
            save(game);
            log.debug("Reached max question! Game ended. maxQuestion: {}, currentQuestion: {}", maxQuestion, currentQuestion);
            throw new NoNextQuestionException("Reached max question! Game ended. maxQuestion:" + maxQuestion );
        }
    }

    public String setCheaterByServer(Integer gameId) {
        Game game = getGameById(gameId);
        List<User> allUsersInGame = userService.getUsersByGame(game);

        // Check if cheater already in game
        for (User userInGame : allUsersInGame) {
            if (userInGame.getRole().equals(roleService.getRoleByName(RoleService.ROLE_CHEATER))) {
                log.debug("Cheater is already in the game: {}", userInGame);
                throw new SetCheaterException("Cheater is already in the game: " + userInGame);
            }
        }

        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_USER), game);
        User cheater;
        if (!users.isEmpty()) {
            Random random = new Random();
            cheater = users.get(random.nextInt(users.size()));
            cheater.setRole(roleService.getRoleByName(RoleService.ROLE_CHEATER));
            userService.update(cheater);
            log.debug("Set cheater by server: {}" , cheater);
            return cheater.getName();
        }
        else {
            log.debug("No users connected - no cheater selected");
            throw new SetCheaterException("No users connected - no cheater selected");
        }
    }

    public void setCheaterByAdmin(String username) {
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

    public void handleCorrectResponseToQuestion(Double value, String username) {
        User user = userService.getUserByname(username);
        Game game ;
        try {
            game = user.getGame();
        } catch (Exception e) {
            throw new GameNotFoundException("Game for user " + username + " was not found");
        }

        Answer answer;
        if (answerService.getAnswerForQuestionByValueAndGameId(value, game.getCurrentQuestionId()) == null) {
            log.debug("Answer is null, answer.value {}, gameId {}", value, game.getCurrentQuestionId());
            return;
        }
        else {
            answer = answerService.getAnswerForQuestionByValueAndGameId(value, game.getCurrentQuestionId());
        }

        CorrectAnswerResponseDto answerDto = answerCnv.convertAnswerToCorrectAnswerResponseDto(username, answer);
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_USER), game);
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_CHEATER), game);
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_ADMIN), game);
        log.debug("handleCorrectResponseToQuestion handleCorrectResponseToQuestion: {}, {}, {}, {} ", game, answer, answerDto, users);
        userNotificationService.sendCorrectAnswerResponseToUsers(answerDto, users);
        cheaterNotificationService.sendCorrectAnswerResponseToCheaters(answerDto, cheaters);
        adminNotificationService.sendCorrectAnswerResponseToAdmins(answerDto, admins);
    }

    public void triggerEndRound(Integer gameId) {
        Game game = getGameById(gameId);
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_USER), game);
        Integer questionId = game.getCurrentQuestionId();
        List<Answer> answers = answerService.getAnswersForQuestionId(questionId);
        List<AnswerDto> answerDtos = answerCnv.convertAnswersToAnswerDtos(answers);
        userNotificationService.sendAllAnswersToUsersInGame(answerDtos, users);
        log.debug("Data to end-round: {}, {}, {}, {}", game,users, answers, answerDtos);
    }

    public void shootPlayer(String username) {
        User user;
        try {
             user = userService.getUserByname(username);
        } catch (Exception e) {
            throw new UserNotFoundException("User " + username + " was not found");
        }
        Game game = null;
        try {
            game = user.getGame();
        } catch (Exception e) {
            throw new GameNotFoundException("Game not found for user " + username);
        }

        List<User> allUsersInGame = game.getUsers();

        boolean wasCheater = user.getRole() == roleService.getRoleByName(ROLE_CHEATER);
        if (!wasCheater) {
            balanceService.handleActionRequestOfMultipleUsers(BalanceAction.DIVIDE, allUsersInGame, 2.0);
        }

        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_ADMIN), game);
        UserDto userDto = userCnv.convertUserToUserDto(user);
        adminNotificationService.notifyAdminAboutShootPlayer(userDto, admins, wasCheater);

        broadcastUserObjectsInGameByUsername(username);
    }

    public void broadcastUserObjectsInGameByUsername(String username) {
        Game game = getGameById(userService.getUserByname(username).getGame().getId());
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_USER), game);
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_CHEATER), game);
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_ADMIN), game);

        List<User> allUsersInGame = game.getUsers();
        List<UserDto> userDtos = userCnv.convertUsersToUserDtos(allUsersInGame);

        userNotificationService.updateUsersObjects(userDtos, users);
        cheaterNotificationService.updateUsersObjects(userDtos, cheaters);
        adminNotificationService.updateUsersObjects(userDtos, admins);
    }

    public void tellPlayerIfHeIsCheater(Integer gameId) {
        Game game = getGameById(gameId);
        List<User> users = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_USER), game);
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(ROLE_CHEATER), game);

        List<User> allUsersInGame = game.getUsers();
        List<UserDto> userDtos = userCnv.convertUsersToUserDtos(allUsersInGame);

        userNotificationService.tellPlayerIfHeIsCheater((new Response(ResponseType.ARE_YOU_CHEATER, "NO")), users);
        cheaterNotificationService.tellPlayerIfHeIsCheater((new Response(ResponseType.ARE_YOU_CHEATER, "YES")), cheaters);
    }


}