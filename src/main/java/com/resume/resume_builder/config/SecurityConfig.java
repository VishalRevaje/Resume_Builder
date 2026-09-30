package com.resume.resume_builder.config;

import com.resume.resume_builder.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserService userService)
            throws Exception {


        http
                .userDetailsService(userService)


                .authorizeHttpRequests(auth -> auth

                        // Public pages
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()


                        // Everything else requires login
                        .anyRequest()
                        .authenticated()
                )


                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .defaultSuccessUrl(
                                "/",
                                true
                        )

                        .failureUrl(
                                "/login?error=true"
                        )

                        .permitAll()
                )


                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .invalidateHttpSession(true)

                        .deleteCookies(
                                "JSESSIONID"
                        )

                        .permitAll()
                );


        return http.build();
    }
}