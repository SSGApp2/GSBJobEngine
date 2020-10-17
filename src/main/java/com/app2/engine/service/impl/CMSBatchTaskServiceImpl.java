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
    public ResponseEntity<String> seizeInfo() {
        String url = "/jobs/seizeInfo";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> legalStatus() {
        String url = "/jobs/legalStatus";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> tblMtLedTask() {
        String url = "/jobs/tblMtLedTask";
          return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> tblMtCourtTask() {
        String url = "/jobs/tblMtCourtTask";
        return getResultByExchange(url);
    }
}
