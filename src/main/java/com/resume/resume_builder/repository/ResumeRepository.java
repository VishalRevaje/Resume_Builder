package com.resume.resume_builder.repository;

import com.resume.resume_builder.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

}