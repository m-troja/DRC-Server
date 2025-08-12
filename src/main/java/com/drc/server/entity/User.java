package com.drc.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Double money;

    @OneToOne
    @JoinColumn(name = "role_name", referencedColumnName = "name")
    private Role role;

    public User(String name, Double money, Role role) {
        this.name = name;
        this.money = money;
        this.role = role;
    }
}
