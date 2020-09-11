package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CBSBatchTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CBSBatchTaskServiceImpl extends AbstractEngineService implements CBSBatchTaskService {
    @Override
    public ResponseEntity<String> createFileTXTRestrictionZLE() {
        String url = "/jobs/createFileTXTRestrictionZLE";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> masterDataCountryTask(String fileName) {
        String url = "/jobs/masterDataCountry?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProvinceTask(String fileName) {
        String url = "/jobs/masterDataProvince?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataDistrictTask(String fileName) {
        String url = "/jobs/masterDataDistrict?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataSubDistrictTask(String fileName) {
        String url = "/jobs/masterDataSubDistrict?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataBranchTask(String fileName) {
        String url = "/jobs/masterDataBranch?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataCostCenterTask(String fileName) {
        String url = "/jobs/masterDataCostCenter?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataWorkingDaysTask(String fileName) {
        String url = "/jobs/masterDataWorkingDays?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataHolidayTask(String fileName) {
        String url = "/jobs/masterDataHoliday?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataOUTask(String fileName) {
        String url = "/jobs/masterDataOU?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataMarketCodeTask(String fileName) {
        String url = "/jobs/masterDataMarketCode?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductGroupTask(String fileName) {
        String url = "/jobs/masterDataProductGroup?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductSubtypeTask(String fileName) {
        String url = "/jobs/masterDataProductSubtype?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductTypeTask(String fileName) {
        String url = "/jobs/masterDataProductType?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataCourtTask(String fileName) {
        String url = "/jobs/masterDataCourt?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataLEOITask(String fileName) {
        String url = "/jobs/masterDataLEOI?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> masterDataTitleTask(String fileName) {
        String url = "/jobs/masterDataTitle?fileName=";
        return getResultByExchange(url+fileName);
    }

    @Override
    public ResponseEntity<String> batchZLETask() {
        String url = "/jobs/batchZLE";
        return getResultByExchange(url);
    }
}
