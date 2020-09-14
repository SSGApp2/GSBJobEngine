package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LitigationUpdateTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LitigationUpdateService litigationUpdateService;

    @Autowired
    private BatchTransactionRepository batchTransactionRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void litigationUpdateTask() {
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        BatchTransaction batchTransaction = null;

        /////////////////////////////////// BKC /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.litigationUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdateBKC");
            batchTransaction.setStatus("S");
            litigationUpdateService.bkc();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        /////////////////////////////////// BKO /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.litigationUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdateBKO");
            batchTransaction.setStatus("S");
            litigationUpdateService.bko();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        // litigationUpdate_CVA
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.litigationUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdateCVA");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdate_CVA();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error litigationUpdate_CVA {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        /////////////////////////////////// CVC /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.batchLitigationUpdate_CVA");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdateCVC");
            batchTransaction.setStatus("S");
            litigationUpdateService.LitigationUpdate_CVC();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        /////////////////////////////////// CVO /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.batchLitigationUpdate_CVO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("batchLitigationUpdateCVO");
            batchTransaction.setStatus("S");
            litigationUpdateService.LitigationUpdate_CVO();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }
}
