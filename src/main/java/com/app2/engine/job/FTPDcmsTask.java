package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.FTPDcmsTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

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
            } else {
                String fileName = response.getBody();
                smbFileService.localFileToRemoteFile(fileName,"DCMS");
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
            } else {
                String pathFile = response.getBody();
                smbFileService.localFileToRemoteFile(pathFile,"DCMS");
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
//    @Scheduled(cron = "0 0 22 * * *") //ss mm hh every day
    public void masterDataTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataTask");

        try {
            String parameterCode = "MASTERDATA_FILE";
            smbFileService.copyRemoteFolderToLocalFolder(parameterCode);

            ftpDcmsTaskService.masterDataTask();

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.info("***************************************");
    }
}
