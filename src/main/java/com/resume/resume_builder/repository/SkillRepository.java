package com.resume.resume_builder.repository;

import com.resume.resume_builder.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

}