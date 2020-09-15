package com.app2.engine.controller;


import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.EmployeeADService;
import com.app2.engine.service.HRDataService;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.service.NotificationTaskService;
import com.app2.engine.service.*;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    HRDataService hrDataService;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DocumentTaskService documentTaskService;

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    NotificationTaskService notificationTaskService;

    @Autowired
    EmployeeADService employeeADService;

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    private WRNService wrnService;

    @Autowired
    CMSBatchTaskService cmsBatchTaskService;

    @GetMapping("/HrRegion")
    public void HrRegion() {
        hrDataService.region();
    }

    @GetMapping("/HrSection")
    public void HrSection() {
        hrDataService.section();
    }

    @GetMapping("/HrPosition")
    public void HrPosition() {
        hrDataService.position();
    }

    @GetMapping("/HrBranch")
    public void HrBranch() {
        hrDataService.branch();
    }

    @GetMapping("/HrLineBusiness")
    public void HrLineBusiness() {
        hrDataService.lineBusiness();
    }

    @GetMapping("/HrUnit")
    public void HrUnit() {
        hrDataService.unit();
    }

    @GetMapping("/HrOrgGroup")
    public void HrOrgGroup() {
        hrDataService.orgGroup();
    }

    @GetMapping("/HrCompany")
    public void HrCompany() {
        hrDataService.company();
    }

    @GetMapping("/HrInterface")
    public void HrInterface() {
        hrDataService.hrInterface();
    }


    @GetMapping("/assignedDocAuto")
    public ResponseEntity<String> assignedDocAuto() {
        return documentTaskService.assignedDocAuto();
    }

    @GetMapping("/houseKeeping")
    public void deleteDataByDay() {
        houseKeepingService.deleteDataByDay();
    }

    @GetMapping("/InsertOrUpdateEmp")
    public void InsertOrUpdateEmp() {
        employeeADService.InsertOrUpdateEmp();
    }

    @GetMapping("/notification")
    public void notification(@RequestParam("processType")String processType) {
        notificationTaskService.notification(processType);
    }

    @GetMapping("/wrnConsent")
    public void wrnConsent(){
        wrnService.wrnConsent();
    }

    @GetMapping("/wrnTDR")
    public void wrnTDR(){
        wrnService.wrnTDR();
    }

    @GetMapping("/litigationStatus")
    public void litigationStatus() {
        ResponseEntity<String> response = cmsBatchTaskService.createFileTXTLegalStatus();
    }

    @GetMapping("/seizeInfo")
    public void seizeInfo(){
        ResponseEntity<String> response = cmsBatchTaskService.createFileTXTSeizeInfo();
    }

    @GetMapping("/lsACN")
    public void lsAcn(){
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File lsACN");
        BatchTransaction batchTransaction = null;
        try {
            String fileName = "LS_ACN_"+codeCurrentDate()+".txt";

//            smbFileService.remoteFileToLocalFile(fileName,"CBS");

            ResponseEntity<String> response = cbsBatchTaskService.lsAcn();

            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.lsACN");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("batchLsACN");
            batchTransaction.setReason(response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody().equals("success")) {
                batchTransaction.setStatus("S");
            }else {
                batchTransaction.setStatus("E");
            }

        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    public String codeCurrentDate() {
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0] + currentDateAr[1] + currentDateAr[2];
        return codeDate;
    }
}
