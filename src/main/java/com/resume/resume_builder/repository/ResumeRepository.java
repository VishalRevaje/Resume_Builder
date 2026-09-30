package com.resume.resume_builder.repository;

import com.resume.resume_builder.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {


    Optional<Resume> findByIdAndUserEmailIgnoreCase(
            Long id,
            String email
    );


    List<Resume> findAllByUserEmailIgnoreCase(
            String email
    );
}