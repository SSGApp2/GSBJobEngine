package com.app2.engine.service;

import com.app2.engine.entity.app.Department;

import java.util.Date;

public interface DepartmentService {
    public Department saveOrUpdateDepartment(String line, Date activeDate);
}
