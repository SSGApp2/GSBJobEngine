package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CMSBatchTaskService {
    ResponseEntity<String> createFileCSVLitigationCVA();
    ResponseEntity<String> createFileTXTSeizeInfo();
    ResponseEntity<String> createFileTXTLegalStatus();
    ResponseEntity<String> batchLagelTask();
    ResponseEntity<String> batchCourtTask();
}
