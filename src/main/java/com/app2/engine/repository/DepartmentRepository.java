package com.app2.engine.repository;

import com.app2.engine.entity.app.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByCode(@Param("code") String code);
}
