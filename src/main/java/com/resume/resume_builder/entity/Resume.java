package com.resume.resume_builder.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String location;

    private String email;

    private String mobile;

    private String linkedin;

    private String github;

    private String fontFamily;

    private Integer fontSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String professionalSummary;

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Education> educations = new ArrayList<>();


    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Skill> skills = new ArrayList<>();


    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Project> projects = new ArrayList<>();


    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Certification> certifications = new ArrayList<>();


    public Resume() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getLinkedin() {
        return linkedin;
    }


    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }


    public String getGithub() {
        return github;
    }


    public void setGithub(String github) {
        this.github = github;
    }


    public String getFontFamily() {
        return fontFamily;
    }


    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }


    public Integer getFontSize() {
        return fontSize;
    }


    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }


    public String getProfessionalSummary() {
        return professionalSummary;
    }


    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }


    public List<Education> getEducations() {
        return educations;
    }


    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }


    public List<Skill> getSkills() {
        return skills;
    }


    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }


    public List<Project> getProjects() {
        return projects;
    }


    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }


    public List<Certification> getCertifications() {
        return certifications;
    }


    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }
}