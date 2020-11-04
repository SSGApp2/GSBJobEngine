package com.app2.engine.repository;

import com.app2.engine.entity.app.AssetSale;
import com.app2.engine.entity.app.GuaranteeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetSaleRepository extends JpaRepository<AssetSale,Long> {
    List<AssetSale> findByGuaranteeInfoOrderBySaleTimeDesc(@Param("guaranteeInfo") GuaranteeInfo guaranteeInfo);
}
