package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AssetGroup extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    private Document document;

    private Double seqGroup;

    private String guaranteeType;

    private String guaranteeDetail;

    private Double amountEstimateBank;

    private Double amountEstimateLG;

    private Double costEstLegalExDepart;

    private String mortgageType;

    private String imageMapFileName;

    private String imageMapRealFileName;

    private String seizedCollateralBroad;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assetGroup")
    private List<AssetSale> assetSales = new ArrayList<>();
}
