package com.app2.engine.controller;

import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
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

    @Autowired
    SmbFileService smbFileService;

    @GetMapping("legalStatus")
    public void legalStatus() {
        ResponseEntity<String> response = cmsBatchTaskService.legalStatus();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CMS");
    }

    @GetMapping("seizeInfo")
    public void seizeInfo(){
        ResponseEntity<String> response = cmsBatchTaskService.seizeInfo();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CMS");
    }

    @GetMapping("tblMtCourtTask")
    public void tblMtCourtTask(){
        ResponseEntity<String> response = cmsBatchTaskService.tblMtCourtTask();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CMS");
    }

    @GetMapping("tblMtLedTask")
    public void tblMtLedTask(){
        ResponseEntity<String> response = cmsBatchTaskService.tblMtLedTask();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CMS");
    }
}
