package com.drc.server.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
@AllArgsConstructor
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @Override
    public String toString() {
        return "Question [id=" + id + ", text=" + text + "]";
    }

    public Question(Integer id, String text) {
        this.id = id;
        this.text = text;
    }
}
