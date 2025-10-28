package com.question.question_sheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.question.question_sheet.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
