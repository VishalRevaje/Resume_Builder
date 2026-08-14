package com.resume.resume_builder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String skillValues;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;


    public Skill() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getSkillValues() {
        return skillValues;
    }


    public void setSkillValues(String skillValues) {
        this.skillValues = skillValues;
    }


    public Resume getResume() {
        return resume;
    }


    public void setResume(Resume resume) {
        this.resume = resume;
    }
}