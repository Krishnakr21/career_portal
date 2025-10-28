package com.question.question_sheet.DTO;

import lombok.Data;

@Data
public class QuestionRequest {
    private String title;
    private String difficulty;
    private String link;
}
