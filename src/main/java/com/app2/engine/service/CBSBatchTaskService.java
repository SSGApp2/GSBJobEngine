package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CBSBatchTaskService {
    ResponseEntity<String> createFileTXTRestrictionZLE();
    ResponseEntity<String> masterDataCountryTask(String fileName);
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
    ResponseEntity<String> masterDataCourtTask(String fileName);
    ResponseEntity<String> masterDataLEOITask(String fileName);
    ResponseEntity<String> masterDataTitleTask(String fileName);
    ResponseEntity<String> batchZLETask();
}
