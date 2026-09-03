package com.resume.resume_builder.controller;

import com.resume.resume_builder.service.ResumeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ResumeService resumeService;

    public HomeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "resumes",
                resumeService.getAllResumes()
        );

        return "index";
    }
}