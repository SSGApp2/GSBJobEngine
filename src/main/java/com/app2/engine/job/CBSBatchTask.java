package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CBSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private String PATHFILETEST = "C:/Users/surap/Project/GSB/toReadFile/";

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    SmbFileService smbFileService;

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
            batchTransaction.setControllerMethod("DebtorTask.createFileTXTRestrictionZLE");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("createFileTxtRestriction");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cbsBatchTaskService.createFileTXTRestrictionZLE();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

            String fileName = response.getBody();
            smbFileService.localFileToRemoteFile(fileName,"CBS");

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
}
