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
public class AccountPayment extends BaseEntity {

    private String legalName;

    private String requestPaymentFileName;

    private String requestPaymentRealFileName;

    private String AccountPaymentCheck;

    private String accountPaymentNo;

    private String defendantType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date accountPaymentDate;

    private String creditorType;

    private Double debtAmount;

    private Double amount;

    private Double interestAmount;

    private Double OtherExpenses;

    private Double TotalAmount;

    private String FullName;

    private String DebtorType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;
}
