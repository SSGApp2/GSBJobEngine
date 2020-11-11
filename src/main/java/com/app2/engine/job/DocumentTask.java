package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DocumentTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    DocumentTaskService documentTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void task1() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start task1");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DocumentTask.task1");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("assignedDocAuto");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = documentTaskService.assignedDocAuto();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
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
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void sendDemandBook() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start sendDemandBook");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DocumentTask.sendDemandBook");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("sendDemandBook Update DocStatus");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = documentTaskService.sendDemandBook();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
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

}
