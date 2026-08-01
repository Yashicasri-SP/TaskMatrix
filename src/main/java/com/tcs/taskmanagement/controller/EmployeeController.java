package com.tcs.taskmanagement.controller;

import com.tcs.taskmanagement.dto.EmployeeDTO;
import com.tcs.taskmanagement.dto.LeaveRequestDTO;
import com.tcs.taskmanagement.dto.TaskDTO;
import com.tcs.taskmanagement.service.EmployeeService;
import com.tcs.taskmanagement.service.LeaveRequestService;
import com.tcs.taskmanagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final LeaveRequestService leaveRequestService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, TaskService taskService, LeaveRequestService leaveRequestService) {
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.leaveRequestService = leaveRequestService;
    }

    // View Profile
    @GetMapping("/profile/{id}")
    public ResponseEntity<EmployeeDTO> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // View Assigned Tasks
    @GetMapping("/tasks/{employeeId}")
    public ResponseEntity<List<TaskDTO>> getAssignedTasks(@PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.getTasksByEmployeeId(employeeId));
    }

    // Update Task Status (PENDING -> IN_PROGRESS -> COMPLETED)
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> body) {
        
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status value is required!");
        }
        TaskDTO updated = taskService.updateTaskStatus(taskId, newStatus);
        return ResponseEntity.ok(updated);
    }

    // --- LEAVE & ATTENDANCE ---

    // Apply for Leave
    @PostMapping("/leaves")
    public ResponseEntity<LeaveRequestDTO> applyLeave(@RequestBody LeaveRequestDTO leaveDTO) {
        LeaveRequestDTO created = leaveRequestService.applyLeave(leaveDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // View My Submitted Leaves
    @GetMapping("/leaves/{employeeId}")
    public ResponseEntity<List<LeaveRequestDTO>> getMyLeaves(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveRequestService.getLeavesByEmployeeId(employeeId));
    }
}
