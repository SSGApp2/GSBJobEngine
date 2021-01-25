package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.EmpDebtAccInfo;

import java.util.List;

public interface EmpDebtAccInfoRepositoryCustom {
   List<EmpDebtAccInfo> findByAccountNo(String accountNo);
   List<EmpDebtAccInfo> findDocumentId(Long documentId);
}
