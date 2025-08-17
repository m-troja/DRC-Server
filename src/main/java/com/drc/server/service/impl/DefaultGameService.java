package com.drc.server.service.impl;

import com.drc.server.dto.AnswerDto;
import com.drc.server.dto.QuestionDto;
import com.drc.server.dto.cnv.AnswerCnv;
import com.drc.server.dto.cnv.QuestionCnv;
import com.drc.server.entity.Game;
import com.drc.server.entity.GameStatus;
import com.drc.server.entity.Question;
import com.drc.server.entity.User;
import com.drc.server.persistence.GameRepo;
import com.drc.server.service.GameService;
import com.drc.server.service.QuestionService;
import com.drc.server.service.RoleService;
import com.drc.server.service.UserService;
import lombok.AllArgsConstructor;
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
    private QuestionService questionService;
    private SimpMessagingTemplate messagingTemplate;
    private GameRepo gameRepo;
    private String clientQuestionEndpoint = "/client/question";
    private AnswerCnv answerCnv;
    private QuestionCnv questionCnv;

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
        log.debug("Broadcasted to {} : {}", clientQuestionEndpoint,  questionDto);
    }

    public void save(Game game) {
        gameRepo.save(game);
    }

    public void sendAnswers(Game game) {

    }

    public DefaultGameService(UserService userService, RoleService roleService, QuestionService questionService,
                              SimpMessagingTemplate messagingTemplate, GameRepo gameRepo, QuestionCnv questionCnv,
                              AnswerCnv answerCnv) {
        this.userService = userService;
        this.roleService = roleService;
        this.questionService = questionService;
        this.messagingTemplate = messagingTemplate;
        this.gameRepo = gameRepo;
        this.questionCnv = questionCnv;
        this.answerCnv = answerCnv;
    }
}