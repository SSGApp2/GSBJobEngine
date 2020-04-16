package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Company extends BaseEntity{

    private String code;

    private String name;

    @Column(name = "status", nullable = false, length = 1)
    public String status;
}
