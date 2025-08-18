package com.drc.server.service.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.QuestionDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.entity.*;
import com.drc.server.persistence.GameRepo;
import com.drc.server.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class DefaultGameService implements GameService {

    private UserService userService;
    private RoleService roleService;
    private AnswerService answerService;
    private QuestionService questionService;
    private SimpMessagingTemplate messagingTemplate;
    private GameRepo gameRepo;
    private AnswerCnv answerCnv;
    private QuestionCnv questionCnv;
    private String clientQuestionEndpoint = "/client/question";
    private String clientAnswerEndpoint = "/queue/answer"; // Sends message for specific user

    public Game startNewGame() {
        Game game = new Game();
        List<User> users = userService.getUsersWithNoGame();
        if (users.isEmpty()){
            return null;
        }
        game.setCurrentQuestionId(1);
        game.setGameStatus(GameStatus.STARTED);
        game.setUsers(users);
        save(game);

        for (User user: users){
            user.setGame(game);
            userService.update(user);
        }
        setCheater(game);
        log.debug("Start new {}  ", game);
        return game;
    }

    public void setCheater(Game game) {
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

    public void sendQuestionToAllClients(Game game) {
        Question question = questionService.getQuestion(game.getCurrentQuestionId());
        QuestionDto questionDto = questionCnv.convertQuestionToQuestionDto(question);
        messagingTemplate.convertAndSend(clientQuestionEndpoint, questionDto);
        log.debug("Sent question to {} : {}", clientQuestionEndpoint,  questionDto);
    }

    public void save(Game game) {
        gameRepo.save(game);
    }

    public void sendAnswers(Game game) {
        List<User> admins = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_ADMIN), game);
        List<User> cheaters = userService.getUsersByRoleAndGame(roleService.getRoleByName(RoleService.ROLE_CHEATER),game);
        log.debug("Admins found: {}", admins);
        log.debug("Cheaters found: {}", cheaters);

        List<Answer> answers = answerService.getAnswersForQuestionId(game.getCurrentQuestionId());
        List<AnswerDto> answerDtos = answerCnv.converAnswersToAnswerDtos(answers);
        log.debug("answers found: {}", answers);
        log.debug("answerDtos found: {}", answerDtos);

        for (User admin : admins) {
            messagingTemplate.convertAndSendToUser(admin.getName(), clientAnswerEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", admin, answerDtos);
        }
        for (User cheater : cheaters) {
            messagingTemplate.convertAndSendToUser(cheater.getName(), clientAnswerEndpoint, answerDtos );
            log.debug("Sent answers to {} : {}", cheater, answerDtos);
        }
    }

    public Game getGameById(Integer id) {
        Game game = gameRepo.findById(id).orElse(null);
        log.debug("Find game by id {} : {} ", id, game);
        return game;
    }

    public DefaultGameService(UserService userService, RoleService roleService, AnswerService answerService, QuestionService questionService,
                              SimpMessagingTemplate messagingTemplate, GameRepo gameRepo, AnswerCnv answerCnv, QuestionCnv questionCnv) {
        this.userService = userService;
        this.roleService = roleService;
        this.answerService = answerService;
        this.questionService = questionService;
        this.messagingTemplate = messagingTemplate;
        this.gameRepo = gameRepo;
        this.answerCnv = answerCnv;
        this.questionCnv = questionCnv;
    }

    public Game triggerNextQuestion(Game game) {
        Integer currentQuestionId = game.getCurrentQuestionId();
        Integer nextQuestionId = ++currentQuestionId;
        log.debug("currentQuestionId {}, nextQuestionId{}", currentQuestionId, nextQuestionId);
        log.debug("Game before change{}", game);
        game.setCurrentQuestionId(nextQuestionId);
        save(game);
        log.debug("Game after change{}", game);

        sendQuestionToAllClients(game);
        sendAnswers(game);
        return game;
    }
}