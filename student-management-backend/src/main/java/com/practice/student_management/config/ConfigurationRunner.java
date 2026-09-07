package com.practice.student_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationRunner implements CommandLineRunner {

    // private final StudentProperties studentProperties;

    public ConfigurationRunner(StudentProperties studentProperties) {
        // this.studentProperties = studentProperties;
    }

    @Override
    public void run(String... args) {

//        System.out.println(
//                "Message: " + studentProperties.getMessage()
//        );
//
//        System.out.println(
//                "Maximum students: " +
//                studentProperties.getMaxStudents()
//        );
//
//        System.out.println(
//                "Registration enabled: " +
//                studentProperties.isRegistrationEnabled()
//        );
    	System.out.println("-----------------------------------------");
    	System.out.println(" Starting Student Management Application");
    	System.out.println("-----------------------------------------");
    }
}