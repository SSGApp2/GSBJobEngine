package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class ComplainantProgress extends BaseEntity {

    private String titleName;

    private String firstName;

    private String lastName;

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

    private String realFileName;

    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentProgress")
    private DocumentProgress documentProgress;
}
