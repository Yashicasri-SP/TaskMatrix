package com.tcs.taskmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String leaveType; // "ANNUAL", "SICK", "CASUAL", "PERSONAL"

    @Column(nullable = false)
    private String startDate; // "YYYY-MM-DD"

    @Column(nullable = false)
    private String endDate; // "YYYY-MM-DD"

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private String status; // "PENDING", "APPROVED", "REJECTED"

    private String appliedOn; // "YYYY-MM-DD"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Default Constructor
    public LeaveRequest() {
    }

    // Parameterized Constructor
    public LeaveRequest(Long id, String leaveType, String startDate, String endDate, String reason, String status, String appliedOn, Employee employee) {
        this.id = id;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.appliedOn = appliedOn;
        this.employee = employee;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(String appliedOn) {
        this.appliedOn = appliedOn;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
