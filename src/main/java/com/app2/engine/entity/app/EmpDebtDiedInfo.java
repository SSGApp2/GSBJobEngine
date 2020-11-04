package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class EmpDebtDiedInfo extends BaseEntity {

    private Integer no;

    private String name;

    private String prefix;

    private String surname;

    private String idCardNumber;

    private String telephoneNumber;

    @Column(length = 4000)
    private String address;

    private String country;

    private String province;

    private String district;

    private String subDistrict;

    private String postcode;

    @Column(length = 1)
    private String type;

    @Transient
    @Getter(AccessLevel.NONE)
    private String personalDocFileName;

    public String getPersonalDocFileName(){
        if(AppUtil.isNotEmpty(personalDocRealFileName)){
            int posOfUnderscore = personalDocRealFileName.indexOf("_");
            return personalDocRealFileName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private String personalDocRealFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String courtOrderFileName;

    public String getCourtOrderFileName(){
        if(AppUtil.isNotEmpty(courtOrderRealFileName)){
            int posOfUnderscore = courtOrderRealFileName.indexOf("_");
            return courtOrderRealFileName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private String courtOrderRealFileName;

    private String blackCaseNumber;

    private String redCaseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empDebtInfo")
    private EmpDebtInfo empDebtInfo;
}
