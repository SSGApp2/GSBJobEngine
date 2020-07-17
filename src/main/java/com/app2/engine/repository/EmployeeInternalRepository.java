package com.app2.engine.repository;

import com.app2.engine.entity.app.EmployeeInternal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface EmployeeInternalRepository extends JpaRepository<EmployeeInternal, Long> {
    EmployeeInternal findByUsername(@Param("username") String username);
}
