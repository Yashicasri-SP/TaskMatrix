package com.tcs.taskmanagement.repository;

import com.tcs.taskmanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Employee> findByRole(String role);
}
