package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class DebtorAccDebtInfo extends BaseEntity {
    @Column(length = 1)
    private String active;

    private String accountNo;

    private String debtorName;

    private String creditType;

    private String creditTypeDesc;

    private String marketCode;

    private String marketDesc;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPaymentDate;

    private Double financialAmount;

    private Double overdueAmount;

    private Double totalInterest;

    private Double principalBalance;

    private Double accruedInterest;

    private Double defaultedInterest;

    private String writeOffFlag;

    private String writeOffFlagStatus;

    private String tdrFlag;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tdrDate;

    private String legalStatus;

    private String legalStatusDesc;

    private String collectionStatus;

    private String collectionStatusDesc;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    private String branchAccount;

    @Column(length = 1)
    private String docCreatedStatus;

    @Column(length = 1)
    private String acctStatus;

    private String acctStatusDesc;

    private String currencyCode;

    private String costCenter;

    private String productClass;

//    @Comment(desc = "D-CD : Certificates of Deposit \n" +
//            "D-DBD : Debit Balance Deposits \n" +
//            "D-DDA : Demand Deposits \n" +
//            "D-ESC : Escrow \n" +
//            "D-SAV : Savings \n" +
//            "D-WASH : Wash \n" +
//            "L-CBL : Credit Balance Loans \n" +
//            "L-CC : Credit Cards \n" +
//            "L-COM : Commercial Loans \n" +
//            "L-DM : Demand Loans \n" +
//            "L-LN : Consumer Loans \n" +
//            "L-MTG : Mortgage Loans  \n" +
//            "L-RC : Revolving Credit Loans")
    private String productGroup;

    private String schdInternalBill;

    private String internalBillNextDue;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPaidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;
}
