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
public class ScheduleEditExtTimeProgress extends BaseEntity {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extDate;

    private String realFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String fileName;

    public String getFileName(){
        if(AppUtil.isNotEmpty(realFileName)){
            int posOfUnderscore = realFileName.indexOf("_");
            return realFileName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;
}
