package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface DCMSBatchTaskService {
    ResponseEntity<String> ACNStartLegal();

    void ACN_END_LEGAL(String date);

    void ACN_END_LEGAL_TOTAL(String date);
}
