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
@EqualsAndHashCode(of = {"id"})
public class AssetDetail extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assetHeader")
    private AssetHeader assetHeader;

    private Integer assetNo;            // ลำดับทรัพย์สืบ

    private String assetType;           // ประเภทหลักประกัน

    private String assetFound;          // รายละเอียดทรัพย์สืบที่พบ

    private String obligation;          // ภาระผูกพัน (1:ติดจำนอง, 2: ปลอดภาระผูกพัน)

    private String mortgageeName;       // ผู้รับจำนอง

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mortgageeDate;         // วันที่รับจำนอง

    private Double amountEstimate;      // ราคาประเมิน

    private String assetSelect;         // ทรัพย์สืบที่เลือก โดย Approve-L1 (Y:เลือก, N: ไม่เลือก)

    private String assetSelectGroup;    // ส่งจัดกลุ่มทรัพย์ที่เลือก โดย Approve-L1 (Y:เลือก, N: ไม่เลือก)

    private String mortgageType;        // สถานะการจำนอง (Y: ติดจำนอง, N:ไม่ติดจำนอง)

    private String holder;              // ผู้ถือกรรมสิทธิ์

    private Double amountLimit;         // วงเงิน
}
