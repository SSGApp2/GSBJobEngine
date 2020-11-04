package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PayOffDebt extends BaseEntity {
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Document document;

    @Column(length = 1)
    private String payStep;

    @Column(length = 1)
    private String sellStep;

    @Column(length = 1)
    private String sendDocument;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;
}
