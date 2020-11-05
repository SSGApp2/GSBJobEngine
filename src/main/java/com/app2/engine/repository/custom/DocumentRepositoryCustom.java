package com.app2.engine.repository.custom;

import java.util.List;
import java.util.Map;

public interface DocumentRepositoryCustom {

    List<Map> findDocumentMovementsCollection();

    List findCurDate(String curDate);

    List findCurDtByRoleByDocument(String curDate, String role, String docID);

    List<Map> findLsAccountList();
}
