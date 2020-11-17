package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.entity.view.DebtorMapAccount;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Debtor extends BaseEntity {
    private String CIF;

    private String fullNameThai;

    private String fullNameEng;

    private String titleName;

    private String titleNameEng;

    private String name;

    private String nameEng;

    private String surname;

    private String surnameEng;

    private String middlename;

    private String middlenameEng;

    @Column(length =13)
    private String idCardNumber;

    private String typePerson;

    private String workPhone;

    private String workExtNumber;

    private String workFax;

    private String homePhone;

    private String homeExtNumber;

    private String mobilePhone;

    private String email;

    @Column(length = 1)
    private String sex;

    @Column(length = 1)
    private String maritalStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    private String validBirthday;

    private String countryResidence;

    private String nationality;

    private String occupation;

    private String subOccupation;

    private String occupationType;

    private String company;

    private String department;

    private String salary;

    private String curAddress;

    private String curCountry;

    private String curProvince;

    private String curDistrict;

    private String curSubDistrict;

    private String curPostcode;

    private String domAddress;

    private String domCountry;

    private String domProvince;

    private String domDistrict;

    private String domSubDistrict;

    private String domPostcode;

    private String workAddress;

    private String workCountry;

    private String workProvince;

    private String workDistrict;

    private String workSubDistrict;

    private String workPostCode;

    private String branchCode;

    private String costCenterCode;

    private String empCode;

    private String resUnitCode;

    private String religion;

    private String education;

    private String totalChild;

    private String custGradeCode;

    private String homeFax;

    private String taxId;

    private String mailFlag;

    private String juristicNo;

    private String esObj;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date juristicRegisterDate;

    private String countryResidenceCd;

    private String totalAssets;

    private String liability;

    private String expense;

    private String netProfit;

    private String cashFlow;

    private String capital;

    private String regCapital;

    private String paidupCap;

    private String annualSales;

    private String netWorth;

    private String riskRating;

    private String creditLine;

    private String numEmployee;

    private String divisionCode;

    private String classCode;

    private String specificCode;

    private String companyCode;

    private String botCustTypeCode;

    private String gsbCustCode;

    private String serviceUserCode;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date customerCreatedDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastContactDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date financialUpdate;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "debtor")
    private List<DebtorAccDebtInfo> debtorAccDebtInfos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "debtor")
    private List<DebtorMapAccount> debtorMapAccounts = new ArrayList<>();
}
