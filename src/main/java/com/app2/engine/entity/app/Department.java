package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Department extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code; //department code

    @Column(length = 1)
    private String type; //type 1: company2: orgGroup3 : lineBusiness4 : zone5 :area6 : branch7 : unit8 subUnit

    private String controlDept; //หน่วยงานแม่

    @Temporal(TemporalType.DATE)
    private Date activeDate; //วันที่ล่าสุดที่ user active

    @Column( length = 1)
    private String status; // A:Active , S:Suspend , I:Inactive , T:Terminated ,R:Retire

}
