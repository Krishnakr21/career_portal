package com.question.question_sheet.DTO;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;
    private String title;
    private String difficulty;
    private String link;
    private boolean status;
    private boolean fav;

    public QuestionResponse(Long id, String title, String difficulty, String link, boolean status, boolean fav) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.link = link;
        this.status = status;
        this.fav = fav;
    }
}
