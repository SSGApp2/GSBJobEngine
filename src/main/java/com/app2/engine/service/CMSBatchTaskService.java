package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CMSBatchTaskService {
    ResponseEntity<String> createFileCSVLitigationCVA();
    ResponseEntity<String> seizeInfo();
    ResponseEntity<String> legalStatus();
    ResponseEntity<String> tblMtLedTask();
    ResponseEntity<String> tblMtCourtTask();
}
