package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.FTPDcmsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FTPDcmsTaskServiceImpl extends AbstractEngineService implements FTPDcmsTaskService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${Api.MovementsCollection}")
    private String APIMovementsCollection;

    @Value("${Api.AccountEndLegalUpdate}")
    private String APIAccountEndLegalUpdate;

    @Value("${Api.MasterData}")
    private String APIMasterData;

    @Override
    public ResponseEntity<String> movementsCollectionTask() {
        return getResultByExchange(APIMovementsCollection);
    }

    @Override
    public ResponseEntity<String> accountEndLegalUpdateTask() {
        return getResultByExchange(APIAccountEndLegalUpdate);
    }

    @Override
    public ResponseEntity<String> masterData() {
        return getResultByExchange(APIMasterData);
    }
}
