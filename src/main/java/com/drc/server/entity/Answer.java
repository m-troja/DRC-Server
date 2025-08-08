package com.drc.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id ;

	private String text;

    @ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;

	String jsessionid;
	@Override
	public String toString() {
		return "Answer [id=" + id + ", text=" + text + "]";
	}
}
