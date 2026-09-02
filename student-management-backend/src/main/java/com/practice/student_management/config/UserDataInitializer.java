package com.practice.student_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.practice.student_management.AppUser;
import com.practice.student_management.AppUserRepository;

@Configuration
public class UserDataInitializer {

    @Bean
    CommandLineRunner createUsers(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (repository.findByUsername("ashutosh").isEmpty()) {

                AppUser user = new AppUser();

                user.setUsername("ashutosh");
                user.setPassword(
                    passwordEncoder.encode("password123")
                );
                user.setRole("USER");

                repository.save(user);
            }

            if (repository.findByUsername("admin").isEmpty()) {

                AppUser admin = new AppUser();

                admin.setUsername("admin");
                admin.setPassword(
                    passwordEncoder.encode("admin123")
                );
                admin.setRole("ADMIN");

                repository.save(admin);
            }
        };
    }
}