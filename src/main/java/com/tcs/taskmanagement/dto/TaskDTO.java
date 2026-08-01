package com.tcs.taskmanagement.dto;

import com.tcs.taskmanagement.model.Task;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String deadline;
    private String status;
    private Long employeeId;
    private String employeeName;

    public TaskDTO() {
    }

    public TaskDTO(Long id, String title, String description, String priority, String deadline, String status, Long employeeId, String employeeName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public static TaskDTO fromEntity(Task task) {
        if (task == null) return null;
        Long empId = task.getEmployee() != null ? task.getEmployee().getId() : null;
        String empName = task.getEmployee() != null ? task.getEmployee().getName() : "Unassigned";
        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getPriority(),
            task.getDeadline(),
            task.getStatus(),
            empId,
            empName
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
