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
public class LoadDebtInfo extends BaseEntity {

    @Column(length = 1)
    private String active;

    private String accountNo;

    private String debtorName;

    private String loanType;

    private String creditType;

    private String maketCode;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPaymentDate;

    private Double financialAmount;

    private Double overdueAmount;

    private Double principleBalance;

    private Double accruedInterest;

    private Double defaultedInterest;

    private Double totalDebt;

    private String writeOffFlag;

    private String TDRFlag;

    private String legalStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;
}
