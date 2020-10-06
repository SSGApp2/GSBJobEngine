package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DocumentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DocumentTaskServiceImpl extends AbstractEngineService implements DocumentTaskService {

    @Value("${Api.AssignedDocAuto}")
    private String APIAssignedDocAuto;

    @Value("${Api.RejectDocNotReceive}")
    private String APIRejectDocNotReceive;

    @Value("${Api.SendDemandBook}")
    private String APISendDemandBook;

    @Override
    public ResponseEntity<String> assignedDocAuto() {
        return getResultByExchange(APIAssignedDocAuto);
    }

    @Override
    public ResponseEntity<String> sendDemandBook() {
        return getResultByExchange(APISendDemandBook);
    }

}
