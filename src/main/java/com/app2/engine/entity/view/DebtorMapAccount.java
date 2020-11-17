package com.app2.engine.entity.view;

import com.app2.engine.entity.app.DebtorAccDebtInfo;
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
@EqualsAndHashCode(of={"id"})
public class DebtorMapAccount extends BaseEntity{

    @Column(length = 1)
    private String active;

    private String accountNo;

    private String loanType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtorAccDebtInfo")
    private DebtorAccDebtInfo debtorAccDebtInfo;
}
