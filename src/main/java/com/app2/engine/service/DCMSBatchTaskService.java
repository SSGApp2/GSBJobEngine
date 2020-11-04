package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

public interface DCMSBatchTaskService {
    ResponseEntity<String> ACNStartLegal();
    void ACN_END_LEGAL(String date);
}
