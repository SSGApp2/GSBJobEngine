package com.app2.engine.repository.custom;

import java.util.List;
import java.util.Map;

public interface CMSRepositoryCustom {
    List<Map> findLegalStatusGuarantee();
    List<Map> findLegalStatusDocHistory(String document);
    List<Map> findLegalStatusAssetSale(String guarantee);
    List<Map> findSeizeInfoGuarantee();
    List<Map> findSeizeInfoDocProgress(String document);
    List<Map> findSeizeInfoConfiscate(String guarantee);
}
