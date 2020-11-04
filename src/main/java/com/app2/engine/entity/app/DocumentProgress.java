package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class DocumentProgress extends BaseEntity {

    @Column(length = 1)
    private String court;

    private String courtName;

    @Column(length = 1)
    private String sendApprove;

    @Column(length = 1)
    private String reqApvType;

    private String reqApvReason;

    @Column(length = 1)
    private String radioSendDemandBook;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocSendDate;

    private String noticDocSendFileName;

    private String noticDocSendRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocReceivedDate;

    private String noticDocFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDate;

    private String noticDocRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocDueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocSendDate2;

    private String noticDocSendFileName2;

    private String noticDocSendRealFileName2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocReceivedDate2;

    private String noticDocFileName2;

    private String noticDocRealFileName2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticDocDueDate2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticNewsDate;

    private String noticNewsFileName;

    private String noticNewsRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticNewsDueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticNewsDate2;

    private String noticNewsFileName2;

    private String noticNewsRealFileName2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticNewsDueDate2;

    private String delayLawsuit;

    private String cancelClaim;

    private String blackCaseNumber;

    private String redCaseNumber;

    private String lawSuitFileName;

    private String lawSuitRealFileName;

    private String lawSuitAfterFileName;

    private String lawSuitRealAfterFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lawSuitSendDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lawSuitAfterSendDate;

    @Column(length = 1)
    private String lawSuitAfterReason;

    @Column(length = 1)
    private String lawSuitBy;

    private String lawSuitNoDefendant;

    @Column(length = 1)
    private String lawSuitStatus;

    private String lawSuitReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date courtOrderDate;

    private String courtOrderFileName;

    private String courtOrderRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date indictmentDate;

    private String indictmentFileName;

    private String indictmentRealFileName;

    private Double capitalInLawsuitsAmount;

    @Column(length = 1)
    private String appealBy;

    @Column(length = 1)
    private String appealSendResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appealNoticDate;

    private String appealFileName;

    private String appealRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleFinalEditDate;

    @Column(length = 1)
    private String scheduleType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleEditDate;

    private String scheduleEditFileName;

    private String scheduleEditRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date defineIssueDate;

    private String defineIssueFileName;

    private String defineIssueRealFileName;

    private String adjRedCaseNumber;

    private String ajdFileName;

    private String ajdRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date adjDate;

    @Column(length = 4000)
    private String eventOther;

    private String eventOtherFileName;

    private String eventOtherRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePalliative;

    private String adjudicateFileName;

    private String adjudicateRealFileName;

    @Column(length = 4)
    private String resultAdjudication;

    @Column(length = 4)
//    @Comment(desc = " คำพิพากษา" +
//            "        A1:ถอนฟ้อง" +
//            "        A2:พิพากษาฝ่ายเดียวเต็มจำนวนคำขอ" +
//            "        A3:พิพากษาฝ่ายเดียวไม่เต็นจำนวนคำขอ" +
//            "        A4:พิพากษาตามสัญญาประนีประนอมยอมความ" +
//            "        A5:ยกฟ้อง (ทั้งคดี)" +
//            "        A6:ยกฟ้อง(ไม่มีหนี้อยู่จริง)" +
//            "        A7:ยกฟ้อง(บางจำเลย)" +
//            "        A8:ยกฟ้อง(คดีขาดอายุความ)" +
//            "    --------------------------------" +
//            "        B1: ไม่รับอุทธรณ์" +
//            "        B2:ยืนตามศาลชั้นต้น" +
//            "        B3:กลับคำพิพากษาชั้นต้น" +
//            "        B4:แก้คำพิพากษาชั้นต้น" +
//            "    --------------------------------" +
//            "        C1:ไม่รับฎีกา" +
//            "        C2:พิพากษายืนตามศาลชั้นต้น" +
//            "        C3:กลับคำพิพากษาศาลชั้นต้น" +
//            "    ---------------------------------" +
//            "        D1: พิทักษ์ทรัพย์เด็ดขาด" +
//            "        D2: ให้ล้มละลาย" +
//            "        D3: ไม่ให้ล้มละลาย")
    private String adjudication;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "th", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date adjDueDate;

    private String adjBlackCaseNumber;

    private String adjDetail;

    private Double adjFeeAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalCaseDate;

    private String finalCaseFileName;

    private String finalCaseRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regulatoryDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regulatoryDueDate;

    private String regulatoryFileName;

    private String regulatoryRealFileName;

    private String executeFileName;

    private String executeRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executeDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankruptDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankruptMeetDate;

    private String bankAppealFileName;

    private String bankAppealRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAppeal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAppealDebtor;

    private String debtorFileName;

    private String debtorRealFileName;

    private String debtorFileNameDebtor;

    private String debtorRealFileNameDebtor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestAppealDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestAppealDateDebtor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date meetingAppealDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date meetingAppealDateDebtor;

    private String adjudicationFileName;

    private String adjudicationRealFileName;

    private String adjudicationFileNameDebtor;

    private String adjudicationRealFileNameDebtor;

    private String preventFileName;

    private String preventRealFileName;

    private String documentStatus;

    private String documentStatusDebtor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date gazetteDate;

    private String gazetteFileName;

    private String gazetteRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date meetPaymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date courtComposeDate;

    @Column(length = 1)
    private String processStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(length = 5)
    private String processComposeStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdjudicateOut;

    private String fileAdjudicateOut;

    private String realFileAdjudicateOut;

    @Column(length = 1)
    private String cancelPayment;

    private String cancelFileName;

    private String cancelRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelDate;

    private String cancelReason;

    @Column(length = 1)
    private String requestRestore;

    private String creditorName;

    private String govermentName;

    private String govermentNameOther;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestRestoreDate;

    private String restoreFileName;

    private String restoreRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date restoreDateOne;

    private String restoreDateOneFileName;

    private String restoreDateOneRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date restoreDateTwo;

    private String restoreDateTwoFileName;

    private String restoreDateTwoRealFileName;

    private String accountListFileName;

    private String accountListRealFileName;

    private String creditorOtherFileName;

    private String creditorOtherRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processDate;

    private String processFileName;

    private String processRealFileName;

    @Column(length = 1)
    private String dissenter;

    private String opposeType;

    private String opposeFileName;

    private String opposeRealFileName;

    private String opposePlanFileName;

    private String opposePlanRealFileName;

    @Column(length = 1)
    private String courtOrderStatus;

    @Column(length = 1)
    private String courtAssetStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCourt;

    private String courtFileName;

    private String courtRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date datePlannerOne;

    @Column(length = 1)
    private String plannerType;

    @Column(length = 1)
    private String plannerChoose;

    private String plannerFileName;

    private String plannerRealFileName;

    @Column(length = 1)
    private String cancelRestoreStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date advertiseDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date advertiseDueDate;

    @Column(length = 1)
    private String bankStatus;

    private String repayFileName;

    private String repayRealFileName;

    private Double amountRepay;

    @Column(length = 1)
    private String repayStatus;

    private Double totalRepay;

    private Double balanceTotalRepay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date argueDueDate;

    @Column(length = 1)
    private String argueStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date argueDate;

    private String argueFileName;

    private String argueRealFileName;

    @Column(length = 1)
    private String argueOrderChoose;

    private String planReFileName;

    private String planReRealFileName;

    private String planReName;

    private String planManageName;

    private String acceptFileName;

    private String acceptRealFileName;

    @Column(length = 1)
    private String acceptStatus;

    private String acceptReason;

    @Column(length = 1)
    private String afterRestoreStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date mtCdtDate1;

    private String mtCdtReport1FileName;

    private String mtCdtReport1RealFileName;

    private String mtCdtPlanFileName;

    private String mtCdtPlanRealFileName;

    private String mtCdtPlanner;

    private String mtCdtManagerPlan;

    private String mtCdtResult;

    private String afterRestoreFileName;

    private String afterRestoreRealFileName;

    private String afterNoRestoreFileName;

    private String afterNoRestoreRealFileName;


    private String afterReportFileName;

    private String afterReportRealFileName;

    @Column(length = 1)
    private String saveStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date afterProcessDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date afterExtendOne;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date afterExtendTwo;

    @Column(length = 1)
    private String successStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date successDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelRehDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date unsuccessfulDate;

    @Column(length = 1)
    private String unsuccessfulStatus;

    private String unsuccessCourtOrderFileName;

    private String unsuccessCourtOrderRealFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date preferentialRequestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date preferentialRightDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date preventRequestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date preventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date averageRequestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date averageDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @Column(length = 1)
    private String sendRequestApproveNextCourt;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<AccountPayment> accountPayments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ReportDocument> reportDocuments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ReportCreditor> reportCreditors = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<DefendantProgress> defendantProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ComplainantProgress> complainantProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<DefendantExtTimeProgress> defendantExtTimeProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<WitnessExamProgress> witnessExamProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<EventDurProgress> eventDurProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<RequestExtTimeProgress> requestExtTimeProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ScheduleEditExtTimeProgress> scheduleEditExtTimeProgresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<CreditorName> creditorNames = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<AccountRepay> accountRepays = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<MeetingCreditor> meetingCreditors = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<CourtOrderDetail> courtOrderDetails = new ArrayList<>();


    @Column(length = 1)
    private String adjSendApprove;

    @Column(length = 1)
    private String adjSendApproveDebt;

    @Column(length = 1)
    private String debtorSendApprove;

    @Column(length = 1)
    private String debtorSendApproveDebt;

    private String bankAppealFileName2;

    private String bankAppealRealFileName2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAppeal2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSupreme;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestAppealDate2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestSupremeDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date meetingAppealDate2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date meetingSupremeDate;

    private String documentStatus2;

    private String documentStatusSupreme;

    private String debtorRealFileName2;

    private String debtorRealFileNameSupreme;

    private String debtorFileName2;

    private String debtorFileNameSupreme;

    private String adjudicationRealFileName2;

    private String adjudicationRealFileNameSupreme;

    private String adjudicationFileName2;

    private String adjudicationFileNameSupreme;

    private String preventRealFileName2;

    private String preventFileName2;

    @Column(length = 1)
    private String supremeSendApprove;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ProcessCompose> processComposes = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date negotiationDate;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<Bankrupt> bankrupts = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<CancelPayment> cancelPayments = new ArrayList<>();


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date advertDate;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date advertDueDate;

    private String advertType;

    private String advertArgueType;

    private String advertRepayFileName;

    private String advertRepayRealFileName;

    private String advertAcc;

    private String advertDebtStatus;

    private Double advertPrinciple;

    private Double advertInterest;

    private Double advertTotalDebt;

    private String advertGuarantorStatus;

    private Double advertPrincipleGuarantor;

    private Double advertInterestGuarantor;

    private Double advertTotalGuarantor;

    private Double advertTotalAmount;

    private Integer advertCountCreditors;

    private Double advertSubDebt;

    private Double capitalLawSuit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date requestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date receiveDate;

    private Double requestAmount;

    private String curEvidenceType;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<ProcessDate> processDates = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<PlannerSequence> plannerSequences = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "documentProgress")
    private List<Arguer> arguers = new ArrayList<>();
}

