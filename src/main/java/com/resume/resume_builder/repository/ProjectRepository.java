package com.resume.resume_builder.repository;

import com.resume.resume_builder.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}