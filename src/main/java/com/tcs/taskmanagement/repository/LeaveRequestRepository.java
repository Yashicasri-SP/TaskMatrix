package com.tcs.taskmanagement.repository;

import com.tcs.taskmanagement.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    
    long countByStatus(String status);
}
