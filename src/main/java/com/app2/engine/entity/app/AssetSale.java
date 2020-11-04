package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AssetSale extends BaseEntity {
    private String listOrder;

    private String propertyType;

    @Column(length = 1)
    private String mortgageType;

    private Double amountForce;

    private Double amountDefineBuy;

    @Column(length = 1)
    private String resultSell;

    @Column(length = 1)
    private String priceReview;

    private Integer saleTime;

    private Integer saleMeetTime;

    private String buy;

    private String nameBuy;

    private Double amountBuy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receiveDate;

    private String eliminate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    private Double amortAmount;

    private String trackAccount;

    @Transient
    @Getter(AccessLevel.NONE)
    private String paymentAccountFileName;

    public String getPaymentAccountFileName() {
        if (AppUtil.isNotEmpty(paymentAccountRealFileName)) {
            int posOfUnderscore = paymentAccountRealFileName.indexOf("_");
            return paymentAccountRealFileName.substring(posOfUnderscore + 1);
        }
        return null;
    }

    private String paymentAccountRealFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String reportLegalFileName;

    public String getReportLegalFileName() {
        if (AppUtil.isNotEmpty(reportLegalRealFileName)) {
            int posOfUnderscore = reportLegalRealFileName.indexOf("_");
            return reportLegalRealFileName.substring(posOfUnderscore + 1);
        }
        return null;
    }

    private String reportLegalRealFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String contractFileName;

    public String getContractFileName() {
        if (AppUtil.isNotEmpty(contractRealFileName)) {
            int posOfUnderscore = contractRealFileName.indexOf("_");
            return contractRealFileName.substring(posOfUnderscore + 1);
        }
        return null;
    }

    private String contractRealFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String extendFileName;

    public String getExtendFileName() {
        if (AppUtil.isNotEmpty(extendRealFileName)) {
            int posOfUnderscore = extendRealFileName.indexOf("_");
            return extendRealFileName.substring(posOfUnderscore + 1);
        }
        return null;
    }

    private String extendRealFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetGroup")
    private AssetGroup assetGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confiscate")
    private Confiscate confiscate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guaranteeInfo")
    private GuaranteeInfo guaranteeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetDetail")
    private AssetDetail assetDetail;

    private String recordBoardStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receiveCashCheckDate;
}
