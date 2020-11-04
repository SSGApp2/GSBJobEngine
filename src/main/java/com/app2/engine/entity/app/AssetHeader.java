package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id"})
public class AssetHeader extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    private Integer assetTime;                      // ครั้งที่

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assetDate;                         // วันที่สืบทรัพย์

    private String assetResult;                     // ผลการสืบทรัพย์ (1:พบ, 2:ไม่พบ)

    private String reportFileName;                  // ชื่อไฟลแนบเอกสารรายงานการสืบทรัพย์

    private String reportRealFileName;              // ชื่อไฟล์แนบเอกสารในระบบรายงานการสืบทรัพย์

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assetHeader")
    private List<AssetDetail> assetDetails = new ArrayList<>();
}
