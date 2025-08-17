package com.drc.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @OneToMany(mappedBy = "game")
//    @JoinColumn(name = "game_id", referencedColumnName = "id")
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;
    private Integer currentQuestionId;
    private GameStatus gameStatus;

    public Game(List<User> users, Integer currentQuestionId, GameStatus gameStatus) {
        this.users = users;
        this.currentQuestionId = currentQuestionId;
        this.gameStatus = gameStatus;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", currentQuestionId=" + currentQuestionId +
                ", gameStatus=" + gameStatus +
                '}';
    }

//    public void addUser(User user) {
//        users.add(user);
//        user.setGame(this);
//    }
}
