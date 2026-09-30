package com.resume.resume_builder.service;

import com.resume.resume_builder.entity.Certification;
import com.resume.resume_builder.entity.Education;
import com.resume.resume_builder.entity.Project;
import com.resume.resume_builder.entity.Resume;
import com.resume.resume_builder.entity.Skill;
import com.resume.resume_builder.entity.User;

import com.resume.resume_builder.repository.ResumeRepository;
import com.resume.resume_builder.repository.UserRepository;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ResumeService {


    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;


    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository) {

        this.resumeRepository = resumeRepository;

        this.userRepository = userRepository;
    }


    // =====================================================
    // SAVE RESUME
    // =====================================================

    @Transactional
    public Resume saveResume(
            Resume resume,
            String userEmail) {


        User user =
                userRepository
                        .findByEmailIgnoreCase(userEmail)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found"
                                        )
                        );


        /*
         * Always assign the logged-in user.
         *
         * This is important because we don't trust
         * user information coming from the HTML form.
         */

        resume.setUser(user);


        // Education

        if (resume.getEducations() != null) {

            for (Education education :
                    resume.getEducations()) {

                education.setResume(resume);
            }
        }


        // Skills

        if (resume.getSkills() != null) {

            for (Skill skill :
                    resume.getSkills()) {

                skill.setResume(resume);
            }
        }


        // Projects

        if (resume.getProjects() != null) {

            for (Project project :
                    resume.getProjects()) {

                project.setResume(resume);
            }
        }


        // Certifications

        if (resume.getCertifications() != null) {

            for (Certification certification :
                    resume.getCertifications()) {

                certification.setResume(resume);
            }
        }


        return resumeRepository.save(resume);
    }


    // =====================================================
    // GET RESUME OF CURRENT USER
    // =====================================================

    @Transactional(readOnly = true)
    public Resume getResumeById(
            Long id,
            String userEmail) {


        return resumeRepository
                .findByIdAndUserEmailIgnoreCase(
                        id,
                        userEmail
                )
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Resume not found"
                                )
                );
    }


    // =====================================================
    // GET ALL RESUMES OF CURRENT USER
    // =====================================================

    @Transactional(readOnly = true)
    public List<Resume> getAllResumesForUser(
            String userEmail) {


        return resumeRepository
                .findAllByUserEmailIgnoreCase(
                        userEmail
                );
    }


    // =====================================================
    // DELETE RESUME OF CURRENT USER
    // =====================================================

    @Transactional
    public void deleteResume(
            Long id,
            String userEmail) {


        Resume resume =
                resumeRepository
                        .findByIdAndUserEmailIgnoreCase(
                                id,
                                userEmail
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Resume not found"
                                        )
                        );


        resumeRepository.delete(resume);
    }
}