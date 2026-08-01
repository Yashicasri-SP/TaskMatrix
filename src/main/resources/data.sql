-- Initial Seed Data for Employees (MySQL & H2 compatible)
INSERT IGNORE INTO employees (id, name, email, password, department, role) VALUES 
(1, 'System Administrator', 'yashicasp422@gmail.com', 'admin123', 'IT Management', 'ADMIN'),
(2, 'Rahul Sharma', 'rahul@tcs.com', 'emp123', 'Software Engineering', 'EMPLOYEE'),
(3, 'Priya Patel', 'priya@tcs.com', 'emp123', 'Quality Assurance', 'EMPLOYEE');

-- Initial Seed Data for Tasks
INSERT IGNORE INTO tasks (id, title, description, priority, deadline, status, employee_id) VALUES 
(1, 'Build Spring Boot REST API', 'Develop REST endpoints for authentication and task management module.', 'HIGH', '2026-08-05', 'IN_PROGRESS', 2),
(2, 'Write Unit & Integration Tests', 'Create JUnit tests for EmployeeService and TaskService.', 'MEDIUM', '2026-08-10', 'PENDING', 3),
(3, 'Frontend UI Modernization', 'Design glassmorphic responsive layout for Employee & Admin portal.', 'HIGH', '2026-08-03', 'COMPLETED', 2);

-- Initial Seed Data for Leave Requests
INSERT IGNORE INTO leave_requests (id, leave_type, start_date, end_date, reason, status, applied_on, employee_id) VALUES
(1, 'ANNUAL', '2026-08-12', '2026-08-15', 'Family vacation', 'PENDING', '2026-07-30', 2),
(2, 'SICK', '2026-08-01', '2026-08-02', 'High fever and doctor consultation', 'APPROVED', '2026-07-31', 3);
