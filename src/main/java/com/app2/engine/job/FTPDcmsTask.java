package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.FTPDcmsTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class FTPDcmsTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    FTPDcmsTaskService ftpDcmsTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    SmbFileService smbFileService;

    @Transactional
//    @Scheduled(cron = "0 0 22 * * *") //ss mm hh every day
    public void movementsCollectionTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File movementsCollectionTask (COLLECTION_STATUS)");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("FTPDcmsTask.movementsCollectionTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("movementsCollectionTask");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = ftpDcmsTaskService.movementsCollectionTask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }else {
                String fileName = response.getBody();
                smbFileService.copyRemoteFileToLocalFile(fileName);
            }
        }catch (Exception e){
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
//    @Scheduled(cron = "0 0 22 * * *") //ss mm hh every day
    public void accountEndLegalUpdateTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File accountEndLegalUpdateTask");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("FTPDcmsTask.accountEndLegalUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("accountEndLegalUpdateTask");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = ftpDcmsTaskService.accountEndLegalUpdateTask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }else{
                String pathFile = response.getBody();
                smbFileService.copyLocalFileToRemoteFile(pathFile);
            }
        }catch (Exception e){
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }
}
