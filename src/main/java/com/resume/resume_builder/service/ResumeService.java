package com.resume.resume_builder.service;

import com.resume.resume_builder.entity.Certification;
import com.resume.resume_builder.entity.Education;
import com.resume.resume_builder.entity.Project;
import com.resume.resume_builder.entity.Resume;
import com.resume.resume_builder.entity.Skill;
import com.resume.resume_builder.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;


    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }


    @Transactional
    public Resume saveResume(Resume resume) {

        if (resume.getEducations() != null) {

            for (Education education : resume.getEducations()) {

                education.setResume(resume);
            }
        }


        if (resume.getSkills() != null) {

            for (Skill skill : resume.getSkills()) {

                skill.setResume(resume);
            }
        }


        if (resume.getProjects() != null) {

            for (Project project : resume.getProjects()) {

                project.setResume(resume);
            }
        }


        if (resume.getCertifications() != null) {

            for (Certification certification :
                    resume.getCertifications()) {

                certification.setResume(resume);
            }
        }


        return resumeRepository.save(resume);
    }


    public Resume getResumeById(Long id) {

        return resumeRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Resume not found with id: " + id
                        )
                );
    }


    public List<Resume> getAllResumes() {

        return resumeRepository.findAll();
    }


    @Transactional
    public void deleteResume(Long id) {

        resumeRepository.deleteById(id);
    }
}