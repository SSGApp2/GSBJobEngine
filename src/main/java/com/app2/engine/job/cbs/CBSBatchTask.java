package com.app2.engine.job.cbs;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class CBSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Transactional
//    @Scheduled(cron = "0 0 22 * * *") //ss mm hh every day
    public void accountEndLegalUpdateTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File accountEndLegalUpdateTask");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.accountEndLegalUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("accountEndLegalUpdateTask");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cbsBatchTaskService.accountEndLegalUpdateTask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String pathFile = response.getBody();
//                smbFileService.localFileToRemoteFile(pathFile,"DCMS");
            }
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
//    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
//    @Scheduled(fixedRate = 30000)
    public void createFileTXTRestrictionZLE() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" createFileTXTRestrictionZLE ");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.createFileTXTRestrictionZLE");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("createFileTxtRestriction");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cbsBatchTaskService.createFileTXTRestrictionZLE();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

            String fileName = response.getBody();
//            smbFileService.localFileToRemoteFile(fileName,"CBS");

        }catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

}
