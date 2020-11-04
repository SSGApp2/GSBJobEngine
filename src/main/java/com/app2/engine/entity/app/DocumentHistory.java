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
@EqualsAndHashCode(of = {"id"})
public class DocumentHistory extends BaseEntity {
    private String docRoleStatus;

    private String userRoleFrom;    //Role ต้นทาง

    private String usernameFrom;    //ผู้ดูแลต้นทาง

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unitFrom")
    private Unit unitFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zoneFrom")
    private Zone zoneFrom;


    private String usernameTo;      //ผู้ดูแลปลายทาง

    private String userRoleTo;      //Role ปลายทาง

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unitTo")
    private Unit unitTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zoneTo")
    private Zone zoneTo;


    private String docStatus;

    private Integer sequence;

    private String userSendBack;

    @Column(length = 4000)
    private String remarkSendBack;

    @Column(length = 4000)
    private String proceedDocument;

//    private String approver;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionTime;

    @Column(length = 1)
    private String lawyerReceive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @Column(name = "dep_for_lead_from")
    private String depForLEADFrom;

    @Column(name = "dep_for_lead_to")
    private String depForLEADTo;

    private String court;

    private String flagNotApprove;
}
