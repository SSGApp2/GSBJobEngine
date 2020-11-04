package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class LitigationCosts extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    private String docType;

    private String caseType;

    @Temporal(TemporalType.DATE)
    private Date costDate;

    private String costType;

    private String listType;

    private Integer installment;

    private String attFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String fileName;

    public String getFileName(){
        if(AppUtil.isNotEmpty(attFileName)){
            int posOfUnderscore = attFileName.indexOf("_");
            return attFileName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private Double amount;

    private String recorder;

    private String approver;

    @Temporal(TemporalType.TIMESTAMP)
    private Date approveDate;

    private String status;
}
