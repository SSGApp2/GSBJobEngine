package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Branch extends BaseEntity{

    private String code;

    private String name;

    private String phoneNumber;

    @Column(name = "status", nullable = false, length = 1)
    private String status;

    private String branchType;

    private String docAutoType;

    private String centerCost;

}
