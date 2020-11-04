package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
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
public class Confiscate extends BaseEntity {
    private String active;

    @Column(length = 1)
    private String createdFor;

    @Column(length = 1)
    private String confiscateType;

    private Double confiscateTime;

    private Date confiscateDate;

    @Column(length = 1)
    private String confiscateArea;

    private String courtAdjudicate;

    private Date confiscateDateArea;

    private String forceCaseFileName;

    private String forceCaseRealFileName;

    private String officeCaseAdjudicate;

    private String officeCaseLocate;

    private String confiscateFileName;

    private String confiscateRealFileName;

    private String imageMapFileName;

    private String imageMapRealFileName;

    private Double costEstLegalOffice;

    private Double costEstExpert;

    private Double costEstLegalExOffice;

    private Double costEstLegalExDepart;

    private Double costEstLegalBank;

    private Double costEstLegalBankDate;

    private Double costEstBoard;

    private String reportFileName;

    private String reportFileRealName;

    private Integer freezeAssetTime;

    private Date freezeAssetDate;

    private String freezeAssetFileName;

    private String freezeAssetRealFileName;

    private String rightsDocumentsFileName;

    private String rightsDocumentsRealFileName;

    private String cancelExecute;

    private String seizeComplainTant;

    private String seizeReject;

    private Date seizeCancelDate;

    private String isSeize;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guaranteeInfo")
    private GuaranteeInfo guaranteeInfo;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "confiscate")
    private List<DefendantFreezeAsset> defendantFreezeAssets = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetDetail")
    private AssetDetail assetDetail;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;
}
