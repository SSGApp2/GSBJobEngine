package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class EmpDebtInfo extends BaseEntity {
    @Column(length = 1)
    private String type;

    @Column(length = 1)
    private String active;

    private String titleName;

    private String titleNameEng;

    private String name;

    private String nameEng;

    private String surname;

    private String surnameEng;

    private String middlename;

    private String middlenameEng;

    private String idCardNumber;

    private String typePerson;

    private String workPhone;

    private String workExtNumber;

    private String fax;

    private String homePhone;

    private String homeExtNumber;

    private String mobilePhone;

    private String email;

    private String sex;

    private String maritalStatus;

    private String birthday;

    private String validBirthday;

    private String countryResidence;

    private String nationality;

    @Column(length = 500)
    private String occupation;

    @Column(length = 500)
    private String subOccupation;

    @Column(length = 500)
    private String occupationType;

    @Column(length = 500)
    private String company;

    @Column(length = 500)
    private String department;

    private String salary;

    @Column(length = 500)
    private String curAddress;

    private String curCountry;

    private String curProvince;

    private String curDistrict;

    private String curSubDistrict;

    private String curPostcode;

    @Column(length = 1)
    private String curActive;

    @Column(length = 500)
    private String domAddress;

    private String domCountry;

    private String domProvince;

    private String domDistrict;

    private String domSubDistrict;

    private String domPostcode;

    @Column(length = 1)
    private String domActive;

    @Column(length = 500)
    private String workAddress;

    private String workCountry;

    private String workProvince;

    private String workDistrict;

    private String workSubDistrict;

    private String workPostcode;

    @Column(length = 1)
    private String workActive;

    @Column(length = 500)
    private String addAddress;

    private String addCountry;

    private String addProvince;

    private String addDistrict;

    private String addSubDistrict;

    private String addPostcode;

    @Column(length = 1)
    private String addActive;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deathDate;

    @Column(length = 1)
    private String deathDateActive;

    @Column(length = 1)
    private String statutoryHeirActive;

    @Column(length = 1)
    private String recipientActive;

    @Column(length = 1)
    private String estateManagerActive;

    @Column(length = 1)
    private String cancelDefendants;

    private String accountNo;

    private String deathCertificate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelSueDate;

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
//            "        A9:ยกฟ้อง (คดีขาดอายุความ)")
    private String adjudication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empDebtInfo")
    private List<EmpDebtCardInfo> empDebtCardInfos = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empDebtInfo")
    private List<EmpDebtDiedInfo> empDebtDiedInfos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventDurProgress")
    private EventDurProgress eventDurProgress;
}
