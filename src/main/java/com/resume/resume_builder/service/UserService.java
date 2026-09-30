package com.resume.resume_builder.service;

import com.resume.resume_builder.entity.User;
import com.resume.resume_builder.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService
        implements UserDetailsService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public User register(
            String name,
            String email,
            String password) {


        String normalizedEmail =
                email.trim().toLowerCase();


        if (userRepository
                .existsByEmailIgnoreCase(
                        normalizedEmail
                )) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }


        User user = new User();


        user.setName(
                name.trim()
        );


        user.setEmail(
                normalizedEmail
        );


        user.setPassword(
                passwordEncoder.encode(password)
        );


        user.setRole("USER");


        return userRepository.save(user);
    }


    public boolean existsByEmail(
            String email) {

        return userRepository
                .existsByEmailIgnoreCase(
                        email.trim()
                );
    }


    @Transactional(readOnly = true)
    public User findByEmail(
            String email) {

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        "User not found"
                                )
                );
    }


    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {


        User user =
                findByEmail(username);


        return org.springframework.security.core.userdetails.User
                .withUsername(
                        user.getEmail()
                )
                .password(
                        user.getPassword()
                )
                .roles(
                        user.getRole()
                )
                .build();
    }
}