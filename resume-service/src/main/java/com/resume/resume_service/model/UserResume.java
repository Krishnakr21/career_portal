package com.resume.resume_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserResume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String customCode;
    private String templateImgUrl;
    private String downloadTemplateLink;
    private String userId;
    private String resumeId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemplateImgUrl(String templateImgUrl) {
        this.templateImgUrl = templateImgUrl;
    }

    public void setDownloadTemplateLink(String downloadTemplateLink) {
        this.downloadTemplateLink = downloadTemplateLink;
    }

}
