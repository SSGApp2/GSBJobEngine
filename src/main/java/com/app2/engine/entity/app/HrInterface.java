package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class HrInterface extends BaseEntity {

    private String code;

    private String company;

    private String orgGroup;

    private String lineBusiness;

    private String zone;

    private String area;

    private String branch;

    private String unit;

    private String subUnit;

    private String costCenter;

    private String description3level;

    private String description1level;

    private String affiliation;

    private String rank;

    private String subRank;

    private String address1;

    private String address2;

    private String district;

    private String province;

    private String postcode;

    private String telephoneNumber;

    private String boundary;

    private String subBoundary;

    private String oldDept;
}
