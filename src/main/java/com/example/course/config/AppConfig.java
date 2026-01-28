package com.example.course.config;

import com.example.course.model.entity.Role;
import com.example.course.model.entity.User;
import com.example.course.repository.RoleRepository;
import com.example.course.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class AppConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
    @Bean
    public CommandLineRunner initApp() {
        return args -> {
            String adminEmail = "admin@gmail.com";

            if (!roleRepository.existsByName("ROLE_USER")) {
                roleRepository.save(Role.builder().name("ROLE_USER")
                        .build());
            }
            if (!roleRepository.existsByName("ROLE_ADMIN")) {
                roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            }
            log.info("Roles initialized successfully");

            Role adminRole = roleRepository.findById(2L).orElseThrow();


            if (!userRepository.existsByEmail(adminEmail)) {
                userRepository.save(
                        User.builder()
                                .fullName("admin")
                                .email(adminEmail)
                                .role(adminRole)
                                .password(passwordEncoder.encode("admin"))
                                .build()
                );
                log.info("Admin initialized successfully");
            }

        };
    }
}
