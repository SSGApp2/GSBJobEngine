package com.app2.engine.entity.app;


import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class EmployeeInternal extends BaseEntity {

    private String fullName;

    private String username;

    private String privateId;

    private String firstName;

    private String lastName;

    private String empGroup;

    private String tempBranch;

    private String autoLoanType;

    private String email;

    private String departmentForLead;

    private String empType;


}
