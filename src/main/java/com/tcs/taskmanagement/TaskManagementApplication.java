package com.tcs.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
        System.out.println("==================================================");
        System.out.println("🚀 Smart Employee Task Management Backend is RUNNING!");
        System.out.println("🌐 Open Frontend in Browser: http://localhost:8080");
        System.out.println("==================================================");
    }
}
