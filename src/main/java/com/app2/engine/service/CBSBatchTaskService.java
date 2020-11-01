package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CBSBatchTaskService {
    void LS_COLLECTION_STATUS(String date);
    ResponseEntity<String> accountEndLegalUpdateTask();
    ResponseEntity<String> createFileTXTRestrictionZLE();
    ResponseEntity<String> stblcntryTask(String fileName);
    ResponseEntity<String> masterDataProvinceTask(String fileName);
    ResponseEntity<String> masterDataDistrictTask(String fileName);
    ResponseEntity<String> masterDataSubDistrictTask(String fileName);
    ResponseEntity<String> masterDataBranchTask(String fileName);
    ResponseEntity<String> masterDataCostCenterTask(String fileName);
    ResponseEntity<String> masterDataWorkingDaysTask(String fileName);
    ResponseEntity<String> masterDataHolidayTask(String fileName);
    ResponseEntity<String> masterDataOUTask(String fileName);
    ResponseEntity<String> masterDataMarketCodeTask(String fileName);
    ResponseEntity<String> masterDataProductGroupTask(String fileName);
    ResponseEntity<String> masterDataProductSubtypeTask(String fileName);
    ResponseEntity<String> masterDataProductTypeTask(String fileName);
    ResponseEntity<String> masterDataTitleTask(String fileName);
    ResponseEntity<String> batchZLETask();
    ResponseEntity<String> lsAcn();
}
