package com.drc.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String httpSessionId;
    private String stompSessionId;
    private Double money;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    public User(String httpSessionId, String name, Double money, Role role) {
        this.httpSessionId = httpSessionId;
        this.name = name;
        this.money = money;
        this.role = role;
    }

    public User(String httpSessionId, String name, Double money) {
        this.httpSessionId = httpSessionId;
        this.name = name;
        this.money = money;
    }


}
