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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit")
    private Unit unit; //ศูนย์ควบคุมและบริหารหนี้

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone")
    private Zone zone; // (ภาค/ฝ่าย)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch")
    private Branch branch; //Branch (สาขา/ศูนย์)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgGroup")
    private OrgGroup orgGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineBusiness")
    private LineBusiness lineBusiness;

}
