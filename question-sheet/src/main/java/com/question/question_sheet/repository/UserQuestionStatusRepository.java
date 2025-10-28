package com.question.question_sheet.repository;

import com.question.question_sheet.model.UserQuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserQuestionStatusRepository extends JpaRepository<UserQuestionStatus, Long> {
    List<UserQuestionStatus> findByUserId(String userId);
    Optional<UserQuestionStatus> findByUserIdAndQsnId(String userId, Long qsnId);
}
