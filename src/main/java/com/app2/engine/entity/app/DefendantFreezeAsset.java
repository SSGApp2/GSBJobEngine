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
@EqualsAndHashCode(of = {"id"})
public class DefendantFreezeAsset extends BaseEntity {
    private Integer numberPerson;

    private String titleName;

    @Column(length = 500)
    private String fullName;

    @Column(length = 1)
    private String defendantStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confiscate")
    private Confiscate confiscate;
}
