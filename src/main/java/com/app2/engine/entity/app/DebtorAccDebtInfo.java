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

    private String accountNo;

    private String marketCode;

    private String marketDesc;

    private String writeOffFlag;

    private String tdrFlag;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tdrDate;

    private String legalStatus;

    private String legalStatusDesc;

    private String collectionStatus;

    private String collectionStatusDesc;

    private String branchAccount;

    @Column(length = 1)
    private String docCreatedStatus;

    private String acctStatusDesc;

    private String productClass;

    private String productGroup;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPaidDate;
}
