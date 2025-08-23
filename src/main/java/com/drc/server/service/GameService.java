package com.drc.server.service;

import com.drc.server.entity.AnswerRequest;
import com.drc.server.entity.Game;
import com.drc.server.entity.User;

public interface GameService {

    Game startNewGame();
    void setCheater(Game game);
    void setCheater(String username);
    void save(Game game);
    Game getGameById(Integer id);
    Game triggerNextQuestion(Game game);
    void deleteAllGames();
    boolean allowNextQuestion(Game game);
}
