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
public class DefendantProgress  extends BaseEntity {

    private String titleName;

    private String fullName;

    private String lastName;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testifyDate;

    private String fileName;

    private String realFileName;

    @Column(length =1)
    private String testimony;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    @Column(length =1)
    private String cancelLawsuit;

    private String remark;

    //เหตุการณ์ระหว่างพิจารณาคดี
    @Column(length =1)
    private String cancelLawsuit2;

    private String remark2;

    private String defendantNo;

    private String remarkAppeal;

    private String remarkSupreme;

    private String evidenceType;
}

