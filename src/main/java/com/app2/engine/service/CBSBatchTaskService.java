package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CBSBatchTaskService {
    void LS_COLLECTION_STATUS(String date);

    ResponseEntity<String> accountEndLegalUpdateTask();

    ResponseEntity<String> createFileTXTRestrictionZLE();

    ResponseEntity<String> stblcntryTask(String fileName);

    ResponseEntity<String> masterDataProvinceTask(String fileName);

    ResponseEntity<String> masterDataDistrictTask(String fileName);

    ResponseEntity<String> masterDataSubDistrictTask(String fileName);

    void masterDataBranchTask(String fileName, String date);

    void masterDataCostCenterTask(String fileName, String date);

    void masterDataWorkingDaysTask(String fileName, String date);

    void masterDataHolidayTask(String fileName, String date);

    void masterDataOUTask(String fileName, String date);

    ResponseEntity<String> masterDataMarketCodeTask(String fileName);

    ResponseEntity<String> masterDataProductGroupTask(String fileName);

    ResponseEntity<String> masterDataProductSubtypeTask(String fileName);

    ResponseEntity<String> masterDataProductTypeTask(String fileName);

    ResponseEntity<String> masterDataTitleTask(String fileName);

    ResponseEntity<String> batchZLETask();

    ResponseEntity<String> lsAcn();
}
