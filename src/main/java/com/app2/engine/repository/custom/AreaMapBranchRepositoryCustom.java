package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.AreaMapBranch;

import java.util.List;

public interface AreaMapBranchRepositoryCustom {
    List<AreaMapBranch> fineByAreaCodeAndBranchCode(String areaCode, String branchCode);
}
