package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ParameterDetail extends BaseEntity {

    private String name;

    private String description;

    private String code;

    private String variable1;

    private String variable2;

    private String variable3;

    private String variable4;

    private String variable5;

    private String variable6;

    private String variable7;

    private String variable8;

    private String variable9;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter")
    private Parameter parameter;
}
