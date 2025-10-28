package com.question.question_sheet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserQuestionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // email
    private Long qsnId;

    private boolean status = false;
    private boolean fav = false;

    public UserQuestionStatus() {}

    public UserQuestionStatus(String userId, Long qsnId) {
        this.userId = userId;
        this.qsnId = qsnId;
    }

}
