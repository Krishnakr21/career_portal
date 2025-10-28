package com.question.question_sheet.controller;

import com.question.question_sheet.DTO.QuestionRequest;
import com.question.question_sheet.DTO.QuestionResponse;
import com.question.question_sheet.model.Question;
import com.question.question_sheet.model.UserQuestionStatus;
import com.question.question_sheet.repository.UserQuestionStatusRepository;
import com.question.question_sheet.service.QuestionService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.question.question_sheet.service.AuthService;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService service;
    private final AuthService authService;
    private final UserQuestionStatusRepository userQsnStatusRepository;

    public QuestionController(QuestionService service, AuthService authService,
                              UserQuestionStatusRepository userQsnStatusRepository) {
        this.service = service;
        this.authService = authService;
        this.userQsnStatusRepository = userQsnStatusRepository;
    }
    

    @PostMapping("/question")
    public ResponseEntity<Question> addQuestion(@RequestBody QuestionRequest request,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid");
        }
        
        String token = authHeader.substring(7);
        String userEmail = authService.extractEmailFromToken(token);
        String userRole = authService.extractRoleFromToken(token);
        System.out.println("User role: " + userRole);
        
        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole) || !userEmail.equals("royinfo29@gmail.com")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to add questions");
        }
        
        return ResponseEntity.ok(service.saveQuestion(request, token));
    }

    @GetMapping("/question")
    public ResponseEntity<List<QuestionResponse>> getUserQuestions(
            @RequestHeader(value = "Authorization") String authHeader) {
    
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid");
        }
    
        String token = authHeader.substring(7);
        String email = authService.extractEmailFromToken(token);
    
        List<Question> allQsns = service.getAllQuestions();  // create this in service
        List<UserQuestionStatus> userStatuses = userQsnStatusRepository.findByUserId(email);
    
        java.util.Map<Long, UserQuestionStatus> statusMap = userStatuses.stream()
                .collect(Collectors.toMap(UserQuestionStatus::getQsnId, s -> s));
    
        List<QuestionResponse> response = new ArrayList<>();
    
        for (Question qsn : allQsns) {
            UserQuestionStatus userStatus = statusMap.get(qsn.getId());
            response.add(new QuestionResponse(
                    qsn.getId(),
                    qsn.getTitle(),
                    qsn.getDifficulty(),
                    qsn.getLink(),
                    userStatus != null ? userStatus.isStatus() : false,
                    userStatus != null ? userStatus.isFav() : false
            ));
        }
    
        return ResponseEntity.ok(response);
    }
    

    @DeleteMapping("/question/{id}")
    public ResponseEntity<Question> deleteQuestion(@PathVariable Long id,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid");
        }
        
        String token = authHeader.substring(7);
        String userRole = authService.extractRoleFromToken(token);
        System.out.println("User role: " + userRole);
        
        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to delete questions");
        }
        
        return ResponseEntity.ok(service.removeQuestion(id));
    }

    @PatchMapping("/question/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UserQuestionStatus updateRequest,
            @RequestHeader("Authorization") String authHeader) {
    
        String token = authHeader.substring(7);
        String userEmail = authService.extractEmailFromToken(token);
    
        UserQuestionStatus existing = userQsnStatusRepository
                .findByUserIdAndQsnId(userEmail, id)
                .orElse(new UserQuestionStatus(userEmail, id));
    
        existing.setStatus(updateRequest.isStatus());
        existing.setFav(updateRequest.isFav());
    
        return ResponseEntity.ok(userQsnStatusRepository.save(existing));
    }
    
}
