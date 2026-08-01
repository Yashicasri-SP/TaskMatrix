package com.tcs.taskmanagement.repository;

import com.tcs.taskmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByEmployeeId(Long employeeId);
    
    long countByStatus(String status);
}
