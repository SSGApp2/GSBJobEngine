package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CBSBatchTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CBSBatchTaskServiceImpl extends AbstractEngineService implements CBSBatchTaskService {

    @Override
    public ResponseEntity<String> createFileTXTRestrictionZLE() {
        String url = "/jobs/createFileTXTRestrictionZLE";
        return getResultByExchange(url);
    }
}
