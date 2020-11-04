package com.app2.engine.repository;

import com.app2.engine.entity.app.AssetDetail;
import com.app2.engine.entity.app.Confiscate;
import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.GuaranteeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfiscateRepository extends JpaRepository<Confiscate,Long>, JpaSpecificationExecutor<Confiscate> {
    List<Confiscate> findConfiscateByDocument(@Param("document") Document document);
    List<Confiscate> findConfiscateByAssetDetail(@Param("assetDetail") AssetDetail assetDetail);
    List<Confiscate> findConfiscateByAssetDetailAndDocument(@Param("assetDetail") AssetDetail assetDetail,@Param("document") Document document);
    List<Confiscate> findAllByGuaranteeInfoOrderByConfiscateDate(@Param("guaranteeInfo") GuaranteeInfo guaranteeInfo);
}
