package com.resume.resume_builder.controller;

import com.resume.resume_builder.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    private final UserService userService;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public DemoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/demo-login")
    public String demoLogin(
            HttpServletRequest request,
            HttpServletResponse response) {

        // Replace this with the EMAIL of your demo account
        String demoEmail = "demo@resumebuilder.com";

        var userDetails = userService.loadUserByUsername(demoEmail);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                request,
                response
        );

        return "redirect:/";
    }
}