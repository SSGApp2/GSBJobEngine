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
public class Document extends BaseEntity {

    private String requester;

    private String requesterRole;

    @Column(nullable = false)
    private String docNumber;

    private String creditAccountNumber;

    private Double principalBalance;

    private Double interest;

    private String docStatus;

    private String docType;

    private String court;

    private String CIF;

    private String prevRole;

    private String prevUserName;

    private String curRole;

    private String curUsername;

    @Column(length = 1)
    private String docCreateStatus;

    private String docRoleStatus;

    @Column(length = 1)
    private String docVerifyStatus;

    private String docVerify;

    private String officeLegal;

    private String courtOrder;

    private String caseRedNo;

    private String caseBlackNo;

    private String plaintiffName;

    private String documentType;

    private String reasonClose;

    private String reasonReject;

    private String reasonWait;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendLawyerDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdjudicate;

    private Double debtBalance;

    @Column(length = 10)
    private String processStatus;

    private String branchCenter;

    @Column(length = 1)
    private String assetContinue;

    private String jobType;

    @Column(length = 1)
    private String lawyerReceive;

    @Column(length = 5)
    private String loanType;

    @Column(length = 1)
    private String typeProcess;

    @Column(length = 4000)
    private String remarkSendBack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit")
    private Unit unit;

    @OneToOne(cascade = CascadeType.ALL)
    private PayOffDebt payOffDebt;

    private String fileRequestCancel;

    private String realFileRequestCancel;

    private String cancelExecuteStatus;

    private String waitForSell;

    private String checkPayment;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<EmpDebtInfo> empDebtInfos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<EmpDebtAccInfo> empDebtAccInfos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<GuaranteeInfo> guaranteeInfos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<DocFileAttach> docFileAttaches = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<DocumentHistory> documentHistories = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<LoadDebtInfo> loadDebtInfos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<AssetHeader> assetHeader = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    private List<LitigationCosts> litigationCosts = new ArrayList<>();

    private Long refDocument;

    private String sendRequestDocTwo;

    private String courtNotApprove;

    private Date sueDate;

    private String userSend;

    private String userSendName;

    private String userSendLastName;

    private String chooseRole;

    private String ChooseUsername;

    private String typeChooseRole;

    private String flagNotApprove;
}
