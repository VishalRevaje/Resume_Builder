package com.resume.resume_builder.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    private String techStack;

    private String githubLink;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;


    public Project() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getTechStack() {
        return techStack;
    }


    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }


    public String getGithubLink() {
        return githubLink;
    }


    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Resume getResume() {
        return resume;
    }


    public void setResume(Resume resume) {
        this.resume = resume;
    }
}