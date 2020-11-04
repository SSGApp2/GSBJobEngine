package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class GuaranteeInfo extends BaseEntity {
    @Column(length = 1)
    private String active;

    private String docNumber;

    private String detail;

    private String type;

    private String subType;

    private String address;

    private Double amount;

    private String subDistrict;

    private String district;

    private String province;

    private String civilCaseFlag;

    private String confiscateFlag;

    private String seizedCollateral;

    private String seizedCollateralLawyer;

    private String seizedCollateralGroup;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluateDate;

    private String holder;

    private String accountNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    private String collateralDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DebtorGuaranteeInfo")
    private DebtorGuaranteeInfo debtorGuaranteeInfo;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "guaranteeInfo")
    private List<Confiscate> confiscates = new ArrayList<>();
}
