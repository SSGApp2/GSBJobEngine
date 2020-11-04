package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class AccountRepay extends BaseEntity {

    private String accountNo;

    private String debtorStatus;

    private Double balanceAmount;

    private Double interestAmount;

    private Double totalAmount;

    private String guarantorStatus;

    private Double guarantorBalanceAmount;

    private Double guarantorInterestAmount;

    private Double guarantorTotalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;
}
