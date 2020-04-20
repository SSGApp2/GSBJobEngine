package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.entity.base.EntityListener;
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
public class Zone extends BaseEntity {
    private String name;
    private String code;
    private String zoneType;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineBusiness")
    private LineBusiness lineBusiness;
}
