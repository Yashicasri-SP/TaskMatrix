package com.tcs.taskmanagement.service;

import com.tcs.taskmanagement.dto.EmployeeDTO;
import com.tcs.taskmanagement.exception.ResourceNotFoundException;
import com.tcs.taskmanagement.model.Employee;
import com.tcs.taskmanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OtpService otpService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, OtpService otpService) {
        this.employeeRepository = employeeRepository;
        this.otpService = otpService;
    }

    // Register new employee or admin
    public EmployeeDTO registerEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with email '" + employee.getEmail() + "' already exists!");
        }
        if (employee.getRole() == null || employee.getRole().trim().isEmpty()) {
            employee.setRole("EMPLOYEE");
        }
        Employee saved = employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(saved);
    }

    // Step 1: Validate credentials only — then generate + email OTP
    public String authenticate(String email, String password) {
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password!"));

        if (!emp.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        // Credentials valid → generate & send OTP
        otpService.generateAndSendOtp(emp.getEmail(), emp.getName());
        return emp.getEmail(); // Return email for frontend to use in step-2
    }

    // Step 2: Verify OTP → grant session
    public EmployeeDTO verifyOtpAndLogin(String email, String otp) {
        boolean valid = otpService.validateOtp(email, otp);
        if (!valid) {
            throw new IllegalArgumentException("Invalid or expired OTP. Please try again.");
        }
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));
        return EmployeeDTO.fromEntity(emp);
    }

    // Fetch all employees
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Fetch employee by ID
    public EmployeeDTO getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return EmployeeDTO.fromEntity(emp);
    }

    // Update Employee details
    public EmployeeDTO updateEmployee(Long id, Employee updatedData) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        existing.setName(updatedData.getName());
        existing.setDepartment(updatedData.getDepartment());
        if (updatedData.getRole() != null) {
            existing.setRole(updatedData.getRole());
        }
        Employee saved = employeeRepository.save(existing);
        return EmployeeDTO.fromEntity(saved);
    }

    // Delete Employee
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
