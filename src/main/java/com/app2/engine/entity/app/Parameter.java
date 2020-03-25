package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Parameter extends BaseEntity {

    private String code;

    private String name;

    @Column(length = 1)
    private String status; // Unused for future

    private String privilege; // RO:Read Only , RW:Read and Write

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parameter")
    private List<ParameterDetail> parameterDetails = new ArrayList<>();

}
