package com.tcs.taskmanagement.dto;

import com.tcs.taskmanagement.model.LeaveRequest;

public class LeaveRequestDTO {
    private Long id;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status;
    private String appliedOn;
    private Long employeeId;
    private String employeeName;
    private String employeeDept;

    public LeaveRequestDTO() {
    }

    public LeaveRequestDTO(Long id, String leaveType, String startDate, String endDate, String reason, String status, String appliedOn, Long employeeId, String employeeName, String employeeDept) {
        this.id = id;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.appliedOn = appliedOn;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeDept = employeeDept;
    }

    public static LeaveRequestDTO fromEntity(LeaveRequest leave) {
        if (leave == null) return null;
        Long empId = leave.getEmployee() != null ? leave.getEmployee().getId() : null;
        String empName = leave.getEmployee() != null ? leave.getEmployee().getName() : "Unknown";
        String empDept = leave.getEmployee() != null ? leave.getEmployee().getDepartment() : "General";
        return new LeaveRequestDTO(
            leave.getId(),
            leave.getLeaveType(),
            leave.getStartDate(),
            leave.getEndDate(),
            leave.getReason(),
            leave.getStatus(),
            leave.getAppliedOn(),
            empId,
            empName,
            empDept
        );
    }

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

    public String getEmployeeDept() {
        return employeeDept;
    }

    public void setEmployeeDept(String employeeDept) {
        this.employeeDept = employeeDept;
    }
}
