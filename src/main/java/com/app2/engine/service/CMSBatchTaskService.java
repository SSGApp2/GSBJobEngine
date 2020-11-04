package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface CMSBatchTaskService {
    ResponseEntity<String> createFileCSVLitigationCVA();

    void SEIZE_INFO(String date);

    void LEGAL_STATUS(String date);

    void TBL_MT_COURT(String date);

    void TBL_MT_LED(String date);
}
