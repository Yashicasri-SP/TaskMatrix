package com.tcs.taskmanagement.service;

import com.tcs.taskmanagement.dto.TaskDTO;
import com.tcs.taskmanagement.exception.ResourceNotFoundException;
import com.tcs.taskmanagement.model.Employee;
import com.tcs.taskmanagement.model.Task;
import com.tcs.taskmanagement.repository.EmployeeRepository;
import com.tcs.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    // Create & Assign Task
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : "MEDIUM");
        task.setDeadline(taskDTO.getDeadline());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : "PENDING");

        if (taskDTO.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(taskDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned employee not found with ID: " + taskDTO.getEmployeeId()));
            task.setEmployee(emp);
        }

        Task saved = taskRepository.save(task);
        return TaskDTO.fromEntity(saved);
    }

    // Fetch all tasks
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Fetch tasks by Employee ID
    public List<TaskDTO> getTasksByEmployeeId(Long employeeId) {
        return taskRepository.findByEmployeeId(employeeId)
                .stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get Task by ID
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        return TaskDTO.fromEntity(task);
    }

    // Update Task Status (Employee operation)
    public TaskDTO updateTaskStatus(Long taskId, String newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setStatus(newStatus.toUpperCase());
        Task updated = taskRepository.save(task);
        return TaskDTO.fromEntity(updated);
    }

    // Update Task Details (Admin operation)
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setDeadline(taskDTO.getDeadline());
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }

        if (taskDTO.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(taskDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + taskDTO.getEmployeeId()));
            task.setEmployee(emp);
        }

        Task saved = taskRepository.save(task);
        return TaskDTO.fromEntity(saved);
    }

    // Delete Task
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    // Summary Statistics for Admin Dashboard
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalTasks = taskRepository.count();
        long pending = taskRepository.countByStatus("PENDING");
        long inProgress = taskRepository.countByStatus("IN_PROGRESS");
        long completed = taskRepository.countByStatus("COMPLETED");
        long totalEmployees = employeeRepository.count();

        stats.put("totalTasks", totalTasks);
        stats.put("pendingTasks", pending);
        stats.put("inProgressTasks", inProgress);
        stats.put("completedTasks", completed);
        stats.put("totalEmployees", totalEmployees);
        return stats;
    }
}
