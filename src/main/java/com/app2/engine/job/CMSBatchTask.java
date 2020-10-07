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
//    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
    public void createFileTXTSeizeInfo() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" createFileTXTSeizeInfo ");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DebtorTask.createFileTXTSeizeInfo");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("seizeInfo");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cmsBatchTaskService.createFileTXTSeizeInfo();
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
//    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
    public void createFileTXTLegalStatus() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" litigationStatus ");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DebtorTask.createFileTXTLitigationStatus");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationStatus");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cmsBatchTaskService.createFileTXTLegalStatus();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }
            String fileName = response.getBody();
//            smbFileService.localFileToRemoteFile(fileName,"CMS");
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

    public String codeCurrentDate(){
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0]+currentDateAr[1]+currentDateAr[2];
        return codeDate;
    }

    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void batchLagelTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File batchLagelTask");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CMSBatchTask.batchLagelTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("batchLagelTask");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cmsBatchTaskService.batchLagelTask();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
                smbFileService.localFileToRemoteFile(fileName,"CMS");
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
