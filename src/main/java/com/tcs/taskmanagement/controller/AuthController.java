package com.tcs.taskmanagement.controller;

import com.tcs.taskmanagement.dto.EmployeeDTO;
import com.tcs.taskmanagement.dto.LoginRequest;
import com.tcs.taskmanagement.dto.OtpResponse;
import com.tcs.taskmanagement.dto.OtpVerifyRequest;
import com.tcs.taskmanagement.model.Employee;
import com.tcs.taskmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final EmployeeService employeeService;

    @Autowired
    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Register new employee / admin
    @PostMapping("/register")
    public ResponseEntity<EmployeeDTO> register(@RequestBody Employee employee) {
        EmployeeDTO registered = employeeService.registerEmployee(employee);
        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }

    /**
     * Step 1 — Login: Validate credentials, generate OTP and send to registered email.
     * Returns OtpResponse { status: "OTP_SENT", email, message }
     */
    @PostMapping("/login")
    public ResponseEntity<OtpResponse> login(@RequestBody LoginRequest loginRequest) {
        String email = employeeService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        OtpResponse response = new OtpResponse(
            "OTP_SENT",
            "A 6-digit OTP has been sent to your registered email address. It expires in 5 minutes.",
            email
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2 — Verify OTP: Validate the OTP and grant full session (returns EmployeeDTO).
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<EmployeeDTO> verifyOtp(@RequestBody OtpVerifyRequest request) {
        EmployeeDTO employee = employeeService.verifyOtpAndLogin(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(employee);
    }
}

