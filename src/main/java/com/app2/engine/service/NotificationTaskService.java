package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface NotificationTaskService {
    ResponseEntity<String> notification(String processType);
}
