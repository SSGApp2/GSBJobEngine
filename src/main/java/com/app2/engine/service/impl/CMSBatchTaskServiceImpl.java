package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CMSBatchTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CMSBatchTaskServiceImpl extends AbstractEngineService implements CMSBatchTaskService{

    @Override
    public ResponseEntity<String> createFileCSVLitigationCVA() {
        String url = "/jobs/createFileCSVLitigationCVA";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> createFileTXTSeizeInfo() {
        String url = "/jobs/createFileTXTSeizeInfo";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> createFileTXTLegalStatus() {
        String url = "/jobs/createFileTXTLegalStatus";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> batchLagelTask() {
        String url = "/jobs/batchLagel";
        return getResultByExchange(url);
    }
}
