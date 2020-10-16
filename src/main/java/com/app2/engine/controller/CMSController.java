package com.app2.engine.controller;

import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.CMSBatchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/cms/")
public class CMSController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CMSBatchTaskService cmsBatchTaskService;

    @GetMapping("litigationStatus")
    public void litigationStatus() {
        ResponseEntity<String> response = cmsBatchTaskService.createFileTXTLegalStatus();
    }

    @GetMapping("seizeInfo")
    public void seizeInfo(){
        ResponseEntity<String> response = cmsBatchTaskService.createFileTXTSeizeInfo();
    }
}
