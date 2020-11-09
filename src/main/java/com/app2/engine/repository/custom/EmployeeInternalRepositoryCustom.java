package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.EmployeeInternal;

import java.util.List;

public interface EmployeeInternalRepositoryCustom {

    List<EmployeeInternal> findEmpAssignedDocAuto(Long branchId, String branchCenter, String loanType, String docAutoType);

}
