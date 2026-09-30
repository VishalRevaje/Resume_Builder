package com.resume.resume_builder.controller;

import com.resume.resume_builder.service.ResumeService;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {


    private final ResumeService resumeService;


    public HomeController(
            ResumeService resumeService) {

        this.resumeService = resumeService;
    }


    @GetMapping("/")
    public String home(
            Model model,
            Authentication authentication) {


        model.addAttribute(
                "resumes",
                resumeService.getAllResumesForUser(
                        authentication.getName()
                )
        );


        return "index";
    }
}