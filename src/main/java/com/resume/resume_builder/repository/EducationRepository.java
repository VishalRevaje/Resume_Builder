package com.resume.resume_builder.repository;

import com.resume.resume_builder.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {

}