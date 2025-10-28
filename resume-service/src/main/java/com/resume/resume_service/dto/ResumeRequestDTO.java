package com.resume.resume_service.dto;

public class ResumeRequestDTO {
    private String name;
    private String latexCode;
    private String templateImgUrl;
    private String downloadTemplateLink;

    public String getName() {
        return this.name;
    }

    public String getLatexCode() {
        return this.latexCode;
    }

    public String getTemplateImgUrl() {
        return this.templateImgUrl;
    }

    public String getDownloadTemplateLink() {
        return this.downloadTemplateLink;
    }

}
