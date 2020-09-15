package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DCMSBatchTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DCMSBatchTaskServiceImpl extends AbstractEngineService implements DCMSBatchTaskService{

    @Override
    public ResponseEntity<String> ACNStartLegal() {
        String url = "/jobs/ACNStartLegal";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> ACNEndLegal() {
        String url = "/jobs/ACNEndLegal";
        return getResultByExchange(url);
    }
}
