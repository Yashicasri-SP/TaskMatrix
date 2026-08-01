package com.tcs.taskmanagement.service;

import com.tcs.taskmanagement.dto.LeaveRequestDTO;
import com.tcs.taskmanagement.exception.ResourceNotFoundException;
import com.tcs.taskmanagement.model.Employee;
import com.tcs.taskmanagement.model.LeaveRequest;
import com.tcs.taskmanagement.repository.EmployeeRepository;
import com.tcs.taskmanagement.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRepository, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    // Submit Leave Request (Employee)
    public LeaveRequestDTO applyLeave(LeaveRequestDTO dto) {
        if (dto.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID is required to apply for leave!");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        LeaveRequest leave = new LeaveRequest();
        leave.setLeaveType(dto.getLeaveType() != null ? dto.getLeaveType() : "ANNUAL");
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setReason(dto.getReason());
        leave.setStatus("PENDING");
        leave.setAppliedOn(LocalDate.now().toString());
        leave.setEmployee(employee);

        LeaveRequest saved = leaveRepository.save(leave);
        return LeaveRequestDTO.fromEntity(saved);
    }

    // Get All Leave Requests (Admin)
    public List<LeaveRequestDTO> getAllLeaves() {
        return leaveRepository.findAll()
                .stream()
                .map(LeaveRequestDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get Leave Requests by Employee ID (Employee view)
    public List<LeaveRequestDTO> getLeavesByEmployeeId(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId)
                .stream()
                .map(LeaveRequestDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Accept / Reject Leave Request (Admin)
    public LeaveRequestDTO updateLeaveStatus(Long leaveId, String newStatus) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        String upperStatus = newStatus.toUpperCase();
        if (!upperStatus.equals("APPROVED") && !upperStatus.equals("REJECTED") && !upperStatus.equals("PENDING")) {
            throw new IllegalArgumentException("Invalid leave status! Must be APPROVED, REJECTED, or PENDING.");
        }

        leave.setStatus(upperStatus);
        LeaveRequest updated = leaveRepository.save(leave);
        return LeaveRequestDTO.fromEntity(updated);
    }
}
