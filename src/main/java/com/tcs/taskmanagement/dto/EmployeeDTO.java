package com.tcs.taskmanagement.dto;

import com.tcs.taskmanagement.model.Employee;

public class EmployeeDTO {
    private Long id;
    private String name;
    private String email;
    private String department;
    private String role;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long id, String name, String email, String department, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    public static EmployeeDTO fromEntity(Employee emp) {
        if (emp == null) return null;
        return new EmployeeDTO(emp.getId(), emp.getName(), emp.getEmail(), emp.getDepartment(), emp.getRole());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
