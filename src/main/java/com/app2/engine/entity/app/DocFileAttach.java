package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.app2.engine.util.AppUtil;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class DocFileAttach extends BaseEntity {

    @Column(length = 1)
    private String active;

    private String name;

    @Column(length = 3)
    private String type;

    @Column(length = 1)
    private String fieldType;

    private String nameFileOther;

    @Transient
    @Getter(AccessLevel.NONE)
    private String fileName;

    public String getFileName() {
        if (AppUtil.isNotEmpty(realFileName)) {
            int posOfUnderscore = realFileName.indexOf("_");
            return realFileName.substring(posOfUnderscore + 1);
        }
        return null;
    }

    private String realFileName;

    private String accountNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtorFileAttach")
    private DebtorFileAttach debtorFileAttach;
}
