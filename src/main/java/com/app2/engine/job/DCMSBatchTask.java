package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class DCMSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DCMSBatchTaskService dcmsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Transactional
//    @Scheduled(cron = "0 30 22 * * *") //ss mm hh every day
    public void createDocumentAutoByCifDebtor() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" createDocumentAutoByCifDebtor ");
        BatchTransaction batchTransaction = null;
        try {
            String fileName = "ACN_STARTLEGAL_"+codeCurrentDate()+".txt";

            smbFileService.remoteFileToLocalFile(fileName,"DCMS");

            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DocumentTask.ACNStartLegal");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("createDocumentAuto");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = dcmsBatchTaskService.ACNStartLegal();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

        } catch (Exception e) {
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
//    @Scheduled(cron = "0 30 22 * * *") //ss mm hh every day
    public void ACNEndLegal() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" ACNEndLegal ");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.ACNEndLegal");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("getAccountUpdateAndEnd(AccountEndLegal)");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = dcmsBatchTaskService.ACNEndLegal();

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

    public String codeCurrentDate(){
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0]+currentDateAr[1]+currentDateAr[2];
        return codeDate;
    }
}
