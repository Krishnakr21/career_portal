package com.question.question_sheet.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String difficulty;
    private String link;
    
}
