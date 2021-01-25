package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DocumentTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DocumentTaskServiceImpl extends AbstractEngineService implements DocumentTaskService {

    @Override
    public ResponseEntity<String> assignedDocAuto() {
        String url = "/jobs/assignedDocAuto";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> sendDemandBook() {
        String url = "/jobs/sendDemandBook";
        return getResultByExchange(url);
    }

}
