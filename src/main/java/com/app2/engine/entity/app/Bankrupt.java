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
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Bankrupt extends BaseEntity {

    private String adjStatus;

    private Date adjDate;

    private String adjFileName;

    private String adjRealFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;

    private String accountNo;
}