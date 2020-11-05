package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CBSBatchTaskService {
    void LS_COLLECTION_STATUS(String date);

    void LS_ACCOUNT_LIST(String date);

    ResponseEntity<String> accountEndLegalUpdateTask();

    ResponseEntity<String> createFileTXTRestrictionZLE();

    void MASTER_DATA_COUNTRY(String date);

    void MASTER_DATA_PROVINCE(String date);

    void MASTER_DATA_DISTRICT(String date);

    void MASTER_DATA_SUB_DISTRICT(String date);

    void MASTER_DATA_BRANCH(String date);

    void MASTER_DATA_COST_CENTER(String date);

    void MASTER_DATA_WORKING_DAYS(String date);

    void MASTER_DATA_HOLIDAY(String date);

    void MASTER_DATA_OU(String date);

    void MASTER_DATA_MARKET_CODE(String date);

    void MASTER_DATA_PRODUCT_GROUP(String date);

    void MASTER_DATA_PRODUCT_SUBTYPE(String date);

    void MASTER_DATA_PRODUCT_TYPE(String date);

    void MASTER_DATA_TITLE(String date);

    void ZLE(String date);

    void LS_ACN(String date);
}
