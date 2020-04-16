package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends BaseEntity {
    private String code;
    private String name;
    private String status;
}
