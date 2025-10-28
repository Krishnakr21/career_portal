package com.question.question_sheet.service;

import com.question.question_sheet.DTO.QuestionRequest;
import com.question.question_sheet.model.Question;
import com.question.question_sheet.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question saveQuestion(QuestionRequest request, String token) {
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setDifficulty(request.getDifficulty());
        question.setLink(request.getLink());
        return repository.save(question);
    }

    public List<Question> getAllQuestions() {
        return repository.findAll();
    }

    public Question removeQuestion(Long id) {
        Question question = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        repository.delete(question);
        return question;
    }
}
