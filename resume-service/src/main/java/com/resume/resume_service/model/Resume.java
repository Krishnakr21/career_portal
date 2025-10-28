package com.resume.resume_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String latexCode;
    private String templateImgUrl;
    private String downloadTemplateLink;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatexCode() {
        return this.latexCode;
    }

    public void setLatexCode(String latexCode) {
        this.latexCode = latexCode;
    }

    public String getTemplateImgUrl() {
        return this.templateImgUrl;
    }

    public void setTemplateImgUrl(String templateImgUrl) {
        this.templateImgUrl = templateImgUrl;
    }

    public String getDownloadTemplateLink() {
        return this.downloadTemplateLink;
    }

    public void setDownloadTemplateLink(String downloadTemplateLink) {
        this.downloadTemplateLink = downloadTemplateLink;
    }

}
