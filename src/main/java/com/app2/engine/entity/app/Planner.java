package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Planner extends BaseEntity {

    @Column(length =1)
    private String plannerType;

    @Column(length = 500)
    private String plannerName;

    @Column(length =1000)
    private String plannerAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plannerSequence")
    private PlannerSequence plannerSequence;
}
