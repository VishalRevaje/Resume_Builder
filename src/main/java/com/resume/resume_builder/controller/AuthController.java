package com.resume.resume_builder.controller;

import com.resume.resume_builder.dto.SignupForm;
import com.resume.resume_builder.service.UserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController {


    private final UserService userService;


    public AuthController(
            UserService userService) {

        this.userService = userService;
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @GetMapping("/login")
    public String login() {

        return "login";
    }


    // =====================================================
    // SIGNUP PAGE
    // =====================================================

    @GetMapping("/signup")
    public String signup(
            Model model) {

        model.addAttribute(
                "signupForm",
                new SignupForm()
        );

        return "signup";
    }


    // =====================================================
    // SIGNUP PROCESS
    // =====================================================

    @PostMapping("/signup")
    public String signup(
            @Valid
            @ModelAttribute("signupForm")
            SignupForm form,

            BindingResult bindingResult) {


        // Check passwords

        if (!form.getPassword()
                .equals(form.getConfirmPassword())) {

            bindingResult.rejectValue(
                    "confirmPassword",
                    "passwordMismatch",
                    "Passwords do not match"
            );
        }


        // Check email

        if (form.getEmail() != null
                && userService.existsByEmail(
                form.getEmail())) {

            bindingResult.rejectValue(
                    "email",
                    "emailExists",
                    "Email is already registered"
            );
        }


        if (bindingResult.hasErrors()) {

            return "signup";
        }


        userService.register(
                form.getName(),
                form.getEmail(),
                form.getPassword()
        );


        return "redirect:/login?registered=true";
    }
}