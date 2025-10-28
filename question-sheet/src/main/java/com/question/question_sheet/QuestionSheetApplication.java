package com.question.question_sheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.question.question_sheet.controller")
public class QuestionSheetApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionSheetApplication.class, args);
    }
}
