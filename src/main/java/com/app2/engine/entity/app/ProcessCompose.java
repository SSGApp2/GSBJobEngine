package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class ProcessCompose extends BaseEntity {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",locale = "th",timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date courtComposeDate;

    private String ProcessComposeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor")
    private Debtor debtor;

    private String accountNo;
}
