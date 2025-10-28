package com.resume.resume_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.resume.resume_service.dto.ResumeRequestDTO;
import com.resume.resume_service.repository.resumeRepository;
import com.resume.resume_service.repository.userResumeRepository;
import com.resume.resume_service.model.Resume;
import com.resume.resume_service.model.UserResume;

@Service
public class ResumeService {        
    private final AuthService authService; 
    private final resumeRepository resumeRepository;
    private final userResumeRepository userResumeRepository;
    
    public ResumeService(AuthService authService, resumeRepository resumeRepository, userResumeRepository userResumeRepository) {
        this.authService = authService;
        this.resumeRepository = resumeRepository;
        this.userResumeRepository = userResumeRepository;
    }
    
    public ResponseEntity<Map<String, String>> addResume(ResumeRequestDTO resumeRequestDTO, String token) { 
        String email = authService.extractEmailFromToken(token);
        if (!email.equals("royinfo29@gmail.com")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized"));
        }
        
        Resume resume = new Resume();
        resume.setName(resumeRequestDTO.getName());
        resume.setLatexCode(resumeRequestDTO.getLatexCode());
        resume.setTemplateImgUrl(resumeRequestDTO.getTemplateImgUrl());
        resume.setDownloadTemplateLink(resumeRequestDTO.getDownloadTemplateLink());
        
        resumeRepository.save(resume);
        return ResponseEntity.ok(Map.of("message", "Resume added successfully"));
    }
    
    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return ResponseEntity.ok(resumes);
    }
    
    public ResponseEntity<Map<String, String>> saveUserResume(Long resumeId, String token) {
        String email = authService.extractEmailFromToken(token);
        
        Resume globalResume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));
        
        List<UserResume> savedResumes = userResumeRepository.findByUserIdAndResumeId(email, resumeId.toString());
        if (savedResumes.isEmpty()) {
            UserResume savedResume = new UserResume();
            savedResume.setName(globalResume.getName());
            savedResume.setCustomCode(globalResume.getLatexCode());
            savedResume.setTemplateImgUrl(globalResume.getTemplateImgUrl());
            savedResume.setDownloadTemplateLink(globalResume.getDownloadTemplateLink());
            savedResume.setUserId(email);
            savedResume.setResumeId(resumeId.toString());
            
            userResumeRepository.save(savedResume);
            return ResponseEntity.ok(Map.of("message", "Resume saved successfully"));
        }
        return ResponseEntity.ok(Map.of("message", "Resume already saved"));
    }
    
    public ResponseEntity<List<UserResume>> getAllSavedResumes(String token) {
        String email = authService.extractEmailFromToken(token);
        List<UserResume> savedResumes = userResumeRepository.findByUserId(email);
        return ResponseEntity.ok(savedResumes);
    }

    public ResponseEntity<Map<String, String>> deleteUserResume(Long resumeId, String token) {
        String email = authService.extractEmailFromToken(token);
        List<UserResume> savedResumes = userResumeRepository.findByUserIdAndResumeId(email, resumeId.toString());
        if (savedResumes.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resume not found"));
        }
        userResumeRepository.delete(savedResumes.get(0));
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully"));
    }

    public ResponseEntity<Map<String, String>> updateUserResumeLatexCode(Long resumeId, String token, String latexCode) {
        String email = authService.extractEmailFromToken(token);
        List<UserResume> savedResumes = userResumeRepository.findByUserIdAndResumeId(email, resumeId.toString());
        if (savedResumes.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resume not found"));
        }
        savedResumes.get(0).setCustomCode(latexCode);
        userResumeRepository.save(savedResumes.get(0));
        return ResponseEntity.ok(Map.of("message", "Resume latex code updated successfully"));
    }
}
