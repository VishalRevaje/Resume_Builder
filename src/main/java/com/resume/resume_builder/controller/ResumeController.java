package com.resume.resume_builder.controller;

import com.resume.resume_builder.entity.Certification;
import com.resume.resume_builder.entity.Education;
import com.resume.resume_builder.entity.Project;
import com.resume.resume_builder.entity.Resume;
import com.resume.resume_builder.entity.Skill;
import com.resume.resume_builder.service.ResumeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ResumeController {

    private final ResumeService resumeService;


    public ResumeController(ResumeService resumeService) {

        this.resumeService = resumeService;
    }


    /* =====================================================
       HOME
       ===================================================== */




    /* =====================================================
       CREATE
       ===================================================== */

    @GetMapping("/resumes/new")
    public String createResume(Model model) {

        Resume resume = new Resume();

        resume.setFontFamily("Arial");

        resume.setFontSize(11);


        // Add one empty record of every section
        resume.getEducations().add(new Education());

        resume.getSkills().add(new Skill());

        resume.getProjects().add(new Project());

        resume.getCertifications().add(new Certification());


        model.addAttribute("resume", resume);

        return "resume-form";
    }


    /* =====================================================
       SAVE
       ===================================================== */

    @PostMapping("/resumes/save")
    public String saveResume(
            @ModelAttribute("resume") Resume resume) {


        // Remove empty education records

        resume.getEducations().removeIf(
                education ->
                        isEmpty(education.getDegree())
                                &&
                                isEmpty(education.getInstitution())
                                &&
                                isEmpty(education.getLocation())
                                &&
                                isEmpty(education.getScore())
        );


        // Remove empty skills

        resume.getSkills().removeIf(
                skill ->
                        isEmpty(skill.getCategory())
                                &&
                                isEmpty(skill.getSkillValues())
        );


        // Remove empty projects

        resume.getProjects().removeIf(
                project ->
                        isEmpty(project.getProjectName())
                                &&
                                isEmpty(project.getDescription())
        );


        // Remove empty certifications

        resume.getCertifications().removeIf(
                certification ->
                        isEmpty(certification.getName())
                                &&
                                isEmpty(certification.getOrganization())
                                &&
                                isEmpty(certification.getDate())
        );


        Resume savedResume =
                resumeService.saveResume(resume);


        return "redirect:/resumes/" + savedResume.getId();
    }


    /* =====================================================
       VIEW
       ===================================================== */

    @GetMapping("/resumes/{id}")
    public String viewResume(
            @PathVariable Long id,
            Model model) {

        Resume resume =
                resumeService.getResumeById(id);

        model.addAttribute(
                "resume",
                resume
        );

        return "resume-preview";
    }


    /* =====================================================
       EDIT
       ===================================================== */

    @GetMapping("/resumes/edit/{id}")
    public String editResume(
            @PathVariable Long id,
            Model model) {

        Resume resume =
                resumeService.getResumeById(id);

        model.addAttribute(
                "resume",
                resume
        );

        return "resume-form";
    }


    /* =====================================================
       DELETE
       ===================================================== */

    @GetMapping("/resumes/delete/{id}")
    public String deleteResume(
            @PathVariable Long id) {

        resumeService.deleteResume(id);

        return "redirect:/";
    }


    /* =====================================================
       EMPTY CHECK
       ===================================================== */

    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}