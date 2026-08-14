package com.resume.resume_builder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "educations")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;

    private String institution;

    private String location;

    private String score;

    private String startYear;

    private String endYear;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;


    public Education() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getDegree() {
        return degree;
    }


    public void setDegree(String degree) {
        this.degree = degree;
    }


    public String getInstitution() {
        return institution;
    }


    public void setInstitution(String institution) {
        this.institution = institution;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getScore() {
        return score;
    }


    public void setScore(String score) {
        this.score = score;
    }


    public String getStartYear() {
        return startYear;
    }


    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }


    public String getEndYear() {
        return endYear;
    }


    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }


    public Resume getResume() {
        return resume;
    }


    public void setResume(Resume resume) {
        this.resume = resume;
    }
}