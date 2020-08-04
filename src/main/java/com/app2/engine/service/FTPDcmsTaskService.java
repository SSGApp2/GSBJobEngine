package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface FTPDcmsTaskService {
    ResponseEntity<String> movementsCollectionTask();
    ResponseEntity<String> accountEndLegalUpdateTask();
    ResponseEntity<String> masterDataTask();
}
