package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class DebtorGuaranteeInfo extends BaseEntity {
    @Column(length = 1)
    private String active;

    private String docNumber;

    private String docTitle;

    private String detail;

    private String type;

    private String typeDesc;

    private String subType;

    private String subTypeDesc;

    @Column(length = 1)
    private String status;

    private String statusDesc;

    private Double amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluateDate;

    private String holder;

    private String accountNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    private String subDistrict;

    private String district;

    private String province;

    private String country;

    private String address;

    private String road;

    private String alley;

    private String legalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtorAccDebtInfo")
    private DebtorAccDebtInfo debtorAccDebtInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateGuarantee;

    private String createdByGuarantee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDateGuarantee;

    private String updatedByGuarantee;

    @Column(length = 1)
    private String isTdr;

    private String tdrRemark;

    @Column(length = 1)
    private String isLegal;

    private String legalRemark;

    @Column(length = 1)
    private String isNpa;

    private String npaRemark;

    @Column(length = 1)
    private String isInsReq;

    @Column(length = 1)
    private String isHold;

    @Column(length = 1)
    private String isSeize;

    @Column(length = 1)
    private String isLevy;

    @Column(length = 1)
    private String isExpropriate;

    @Column(length = 1)
    private String isSeizeCancel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seizeCancelDate;

    @Column(length = 1)
    private String isLevyCancel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date levyCancelDate;

    @Column(length = 1)
    private String isExpropriateCancel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expropriateCancelDate;

    private String ravangNo;

    private String landNo;

    private String bookNo;

    private String pageNo;

    private String pageSurvey;

    private String areaRai;

    private String areaNgan;

    private String areaTarangwa;

    private String locationPosition;

    private String locationCondition;

    private String docLocation;

    private String remark;

    @Column(length = 1)
    private String isMergeLand;

    @Column(length = 1)
    private String isVideLand;

    @Column(length = 1)
    private String isBuildingAcross;

    @Column(length = 1)
    private String landIsPartPledged;

    private String botCustCode;

    private String subBotCustCode;

    private String propertyType;

    @Column(length = 1)
    private String isNoncrm;

    private String cmsAppraisalCode;

    @Column(length = 1)
    private String isDomesticAppraisal;

    @Column(length = 1)
    private String isConsolidated;

    @Column(length = 1)
    private String isMain;

    private String mainColl;

    private String appraisalBy;

    @Column(length = 1)
    private String approvalBy;

    private String approvalDeptCode;

    private String approvalCommittee;

    private String appraisalOfficer1;

    private String appraisalOfficer2;

    private String appraisalOfficer3;

    private String appraisalCompany;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loanRequestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appraisalDate;

    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date exchangeDate;

    private String exchangeRate;

    private Double firstEvaluationPrice;

    private Double firstAppraisalBuilding;

    private Double firstAppraisalTotal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluateFirstDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluateNextDate;

    private Double marketPrice;

    private Double contractPrice;

    private Double capitalPrice;

    private Double landGovApprTotal;

    private Double landGovApprStart;

    private Double landGovApprEnd;

    private String landGovApprStartYear;

    private String landGovApprEndYear;

    private Double syndApprVal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date landExectionApprDt;

    private Double appraisalLand;

    private Double appraisalBuilding;

    private Double appraisalTotal;

    private Double appraisalLandPartial;

    private Double appraisalBuildingPartial;

    private Double appraisalTotalPartial;

    private String reasonCode;

    private String realLiquidityCd;

    private String appraisalType;

    private Double originalApprVal;

    private Double oldSequestrationAmt;

    private String landOwnerPortion;

    private String landTotalPortion;

    private String landOwnerRai;

    private String landOwnerNgan;

    private String landOwnerTarangwa;

    private String landLocation1;

    private String landLocation2;

    private String landLocation3;

    private String partialRemark;

    private String buildingNo;

    private String buildingRegisNo;

    private String buildingRmNo;

    private String buildingStorey;

    private String buildingVol1;

    private String buildingVol2;

    private String buildingVol3;

    private String buildingCountUnit;

    private String buildingType;

    private String buildingName;

    @Column(length = 1)
    private String buildingLandCondition;

    private String buildingConstruction;

    private String buildingLandNo;

    private String realBuildingArea;

    private String countUnit;

    private String buildingFloor;

    private String buildingDecoration;

    private String buildingVal;

    private String buildingGroupNo;

    private String buildingCategory;

    private String buildingOwnership;

    private String buildingModel;

    private String isDecorated;

    private String buildingAgeYear;

    private String buildingAgeMonth;

    private String wall;

    private String floor;

    private String materialSurface;

    private String buildingStructure;

    private String roof;

    private String roofStructure;

    private String otherBuildingLeft;

    private String otherBuildingRight;

    private String otherBuildingFront;

    private String otherBuildingBack;

    private String occupancy;

    private String occupancyOther;

    // ============== Type 8 : เงินฝาก ==============
    private String depositDoc;

    private String fiInstCode;

    private String fiInstDesc;

    @Column(length = 1)
    private String isForOthInst;

    @Column(length = 1)
    private String isRefinanceInst;

    private String depositAccount;

    private String holdCode;

    private String holdSeq;

    private String prevHoldCode;

    private String prevHoldAmt;

    // ============== Type 15 : บุคคล/นิติบุคคล ค้ำประกัน ==============
    private String guarantorCode;

    private String debtBph;

    private String debtBphExt;

    private String debtHph;

    private String debtMph;

    private String debtName;

    private String debtPostcode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date issueDate;

    private String debtBusDesc;

    private String debtBusCode;

    private String debtBusGroup;

    private String debtBusSection;

    private String debtBusThSpec;

    private String debtBusThSpecBot;

    private String debtorTypeCode;

    private String debtorTypeDesc;

    private String debtorPaymentCode;

    private String debtorPaymentDesc;

    private String guaAcn;

    private String guaTitle;

    private String guaFname;

    private String guaLname;

    // ============== Type 21 : สลากออมสิน ==============
    private String salakCertCode;

    private String salakNo;

    private String salakCcid;

    private String salakCif;

    private String salakCustType;

    private String salakDeptTitle;

    private String salakOwnerThFname;

    private String salakOwnerThLname;

    private String salakBal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date salakExpDate;

    private String salakGroup;

    private String salakHightNo;

    private String salakLot;

    private String salakLowno;

    private String salakPeriod;

    private String salakUnit;
}
