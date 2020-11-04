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
@EqualsAndHashCode(of = {"id"})
public class PlannerSequence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    private Integer sequence;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.DATE)
    private Date datePlanner;

    @Column(length = 1)
    private String plannerChoose;

    @Column(length = 1)
    private String plannerIsBank;

    @Column(length = 1)
    private String plannerIsCreditor;

    @Column(length = 1)
    private String plannerIsOther;

    private String plannerFileName;

    private String plannerRealFileName;

    @Column(length = 1)
    private String courtCancel;

    private String courtCancelFileName;

    private String courtCancelRealFileName;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "plannerSequence")
    private List<Planner> planners = new ArrayList<>();
}

