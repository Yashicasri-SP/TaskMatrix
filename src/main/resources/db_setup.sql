-- Create Database
CREATE DATABASE IF NOT EXISTS task_management_db;
USE task_management_db;

-- 1. Employees Table
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    role VARCHAR(50) NOT NULL
);

-- 2. Tasks Table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL,
    deadline VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL
);

-- 3. Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_type VARCHAR(50) NOT NULL,
    start_date VARCHAR(50) NOT NULL,
    end_date VARCHAR(50) NOT NULL,
    reason TEXT,
    status VARCHAR(50) NOT NULL,
    applied_on VARCHAR(50),
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- 4. Seed Employees
INSERT IGNORE INTO employees (id, name, email, password, department, role) VALUES 
(1, 'System Administrator', 'admin@tcs.com', 'admin123', 'IT Management', 'ADMIN'),
(2, 'Rahul Sharma', 'rahul@tcs.com', 'emp123', 'Software Engineering', 'EMPLOYEE'),
(3, 'Priya Patel', 'priya@tcs.com', 'emp123', 'Quality Assurance', 'EMPLOYEE');

-- 5. Seed Tasks
INSERT IGNORE INTO tasks (id, title, description, priority, deadline, status, employee_id) VALUES 
(1, 'Build Spring Boot REST API', 'Develop REST endpoints for authentication and task management module.', 'HIGH', '2026-08-05', 'IN_PROGRESS', 2),
(2, 'Write Unit & Integration Tests', 'Create JUnit tests for EmployeeService and TaskService.', 'MEDIUM', '2026-08-10', 'PENDING', 3),
(3, 'Frontend UI Modernization', 'Design glassmorphic responsive layout for Employee & Admin portal.', 'HIGH', '2026-08-03', 'COMPLETED', 2);

-- 6. Seed Leave Requests
INSERT IGNORE INTO leave_requests (id, leave_type, start_date, end_date, reason, status, applied_on, employee_id) VALUES
(1, 'ANNUAL', '2026-08-12', '2026-08-15', 'Family vacation', 'PENDING', '2026-07-30', 2),
(2, 'SICK', '2026-08-01', '2026-08-02', 'High fever and doctor consultation', 'APPROVED', '2026-07-31', 3);
