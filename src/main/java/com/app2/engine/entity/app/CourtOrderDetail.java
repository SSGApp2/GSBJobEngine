package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class CourtOrderDetail extends BaseEntity {

    private Integer courtOrderTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date permissDate;

    private String permissFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String fileName;

    public String getFileName(){
        if (AppUtil.isNotEmpty(permissFileRealName)){
            int posOfUnderscore = permissFileRealName.indexOf("_");
            return permissFileRealName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private String permissFileRealName;

    private Double amount;

    private Double interestAmount;

    private Double totalAmount;

    @Column(length =1)
    private String permissStatus;

    @Column(length =1)
    private String documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;
}
