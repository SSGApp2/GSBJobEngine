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
public class EventDurProgress extends BaseEntity {

    private String time;

    @Column(length = 1)
    private String evenType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private String bankRealFileName;

    @Transient
    @Getter(AccessLevel.NONE)
    private String bankFileName;

    public String getBankFileName(){
        if(AppUtil.isNotEmpty(bankRealFileName)){
            int posOfUnderscore = bankRealFileName.indexOf("_");
            return bankRealFileName.substring(posOfUnderscore+1);
        }
        return null;
    }

    private String result; //ผลพิจารณาจากศาล 1:ดำเนินการต่อ 2: ยกฟ้อง 3: ปรับปรุงโครงสร้างหนี้

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;

    private String eventOther;

    @Transient
    @Getter(AccessLevel.NONE)
    private String eventDurFileName;

    private String eventDurRealFileName;

    public String getEventDurFileName(){
        if(AppUtil.isNotEmpty(eventDurRealFileName)){
            int posOfUnderscore = eventDurRealFileName.indexOf("_");
            return eventDurRealFileName.substring(posOfUnderscore+1);
        }
        return null;
    }
}
