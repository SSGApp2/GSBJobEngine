package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class CMSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    CMSBatchTaskService cmsBatchTaskService;

    @Autowired
    SmbFileService smbFileService;

    @Transactional
    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
    public void seizeInfo() {
        // รับข้อมูลการยึดทรัพย์ (CollSeizeDetail) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("SeizeInfo ");
        LOGGER.info("Start create file get confiscation information. ");
        LOGGER.info("File : SEIZEINFO_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DebtorTask.SeizeInfo");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("SEIZEINFO_YYYYMMDD.txt");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cmsBatchTaskService.seizeInfo();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }
            String fileName = response.getBody();
            smbFileService.remoteFileToLocalFile(fileName,"CMS");

        }catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
    public void legalStatus() {
        // รับข้อมูลสถานะ Litigation (LitigationStatus) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("legalStatus ");
        LOGGER.info("Start create file get Litigation status.");
        LOGGER.info("File : LEGAL_STATUS_YYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DebtorTask.legalStatus");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LEGAL_STATUS_YYYMMDD.txt");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cmsBatchTaskService.legalStatus();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }
            String fileName = response.getBody();
            smbFileService.localFileToRemoteFile(fileName,"CMS");
        }catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

//    @Transactional
//    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
//    public void createFileCSVLitigationCVA() {
//        LOGGER.info("**************************************************************************");
//        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
//        LOGGER.info(" createFileCSVLitigationCVA ");
//        BatchTransaction batchTransaction = null;
//        try {
//            batchTransaction = new BatchTransaction();
//            batchTransaction.setControllerMethod("DebtorTask.createFileCSVLitigationCVA");
//            batchTransaction.setStartDate(DateUtil.getCurrentDate());
//            batchTransaction.setName("createFileCSVLitigationCVA");
//            batchTransaction.setStatus("S");
//
//            ResponseEntity<String> response = cmsBatchTaskService.createFileCSVLitigationCVA();
//            if (!response.getStatusCode().is2xxSuccessful()) {
//                batchTransaction.setStatus("E");
//                batchTransaction.setReason(response.getBody());
//            }
//            String pathName = response.getBody();
//            String[] fileNameAr = pathName.split("/");
//            String fileName = fileNameAr[fileNameAr.length-1];
//
//            smbFileService.remoteFileToLocalFile(fileName,"CMS");
//
//        }catch (Exception e) {
//            batchTransaction.setStatus("E");
//            batchTransaction.setReason(e.getMessage());
//            LOGGER.error("Error {}", e.getMessage());
//        } finally {
//            batchTransaction.setEndDate(DateUtil.getCurrentDate());
//            batchTransactionRepository.saveAndFlush(batchTransaction);
//        }
//        LOGGER.info("**************************************************************************");
//    }

    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void tblMtLedTask() {
        // ข้อมูลสำนักงานบังคับคดี
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File get Legal Execution Office.");
        LOGGER.info("File : TBL_MT_LED_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CMSBatchTask.tblMtLedTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("TBL_MT_LED_YYYYMMDD.txt");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cmsBatchTaskService.tblMtLedTask();
          
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
                smbFileService.remoteFileToLocalFile(fileName,"CMS");
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
  
  
    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void tblMtCourtTask() {
        // ข้อมูลศาล : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File get court data.");
        LOGGER.info("File : TBL_MT_COURT_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CMSBatchTask.tblMtCourtTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("TBL_MT_COURT_YYYYMMDD.txt");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cmsBatchTaskService.tblMtCourtTask();
          
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
                smbFileService.remoteFileToLocalFile(fileName,"CMS");
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
}
