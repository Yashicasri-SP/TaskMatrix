package com.tcs.taskmanagement.controller;

import com.tcs.taskmanagement.dto.EmployeeDTO;
import com.tcs.taskmanagement.dto.LeaveRequestDTO;
import com.tcs.taskmanagement.dto.TaskDTO;
import com.tcs.taskmanagement.model.Employee;
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
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final LeaveRequestService leaveRequestService;

    @Autowired
    public AdminController(EmployeeService employeeService, TaskService taskService, LeaveRequestService leaveRequestService) {
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.leaveRequestService = leaveRequestService;
    }

    // Dashboard Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = taskService.getDashboardStats();
        long pendingLeaves = leaveRequestService.getAllLeaves().stream().filter(l -> "PENDING".equalsIgnoreCase(l.getStatus())).count();
        stats.put("pendingLeaves", pendingLeaves);
        return ResponseEntity.ok(stats);
    }

    // --- EMPLOYEE MANAGEMENT ---

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody Employee employee) {
        EmployeeDTO created = employeeService.registerEmployee(employee);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        EmployeeDTO updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        Map<String, String> response = Map.of("message", "Employee deleted successfully!");
        return ResponseEntity.ok(response);
    }

    // --- TASK MANAGEMENT ---

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO created = taskService.createTask(taskDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updated = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        Map<String, String> response = Map.of("message", "Task deleted successfully!");
        return ResponseEntity.ok(response);
    }

    // --- LEAVE MANAGEMENT ---

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaves() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaves());
    }

    @PatchMapping("/leaves/{id}/status")
    public ResponseEntity<LeaveRequestDTO> updateLeaveStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            throw new IllegalArgumentException("Status value is required!");
        }
        LeaveRequestDTO updated = leaveRequestService.updateLeaveStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
