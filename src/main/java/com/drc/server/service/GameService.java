package com.drc.server.service;

import com.drc.server.entity.Game;

public interface GameService {

    Game startNewGame();
    void setCheaterByAdmin(String username);
    String setCheaterByServer(Integer gameId);
    void save(Game game);
    Game getGameById(Integer id);
    Game triggerNextQuestion(Game game);
    void deleteAllGames();
    boolean allowNextQuestion(Game game);
}
