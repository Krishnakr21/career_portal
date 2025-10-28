package com.resume.resume_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.resume.resume_service.model.UserResume;

public interface userResumeRepository extends JpaRepository<UserResume, Long> {
    List<UserResume> findByUserId(String userId);
    List<UserResume> findByUserIdAndResumeId(String userId, String resumeId);
}
