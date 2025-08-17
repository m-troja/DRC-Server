package com.drc.server.service;

import com.drc.server.entity.Game;

public interface GameService {
    Game startNewGame();
    void setCheater(Game game);
    void sendQuestionToAllClients(Game game);
    void save(Game game);
    void sendAnswers(Game game);
    Game getGameById(Integer id);
}
