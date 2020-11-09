package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.WRNService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class WRNTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WRNService wrnService;

    @Autowired
    private BatchTransactionRepository batchTransactionRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void wrnTask() {
        LOGGER.info("***************** WRN *********************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        BatchTransaction batchTransaction = null;
        /////////////////////////////////// WRN_CONSENT /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("WRNTask.wrnTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("wrnConsent");
            batchTransaction.setStatus("S");
            wrnService.WRN_CONSENT(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        /////////////////////////////////// WRN_TDR /////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("WRNTask.wrnTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("wrnTDR");
            batchTransaction.setStatus("S");
            wrnService.WRN_TDR(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }
}
