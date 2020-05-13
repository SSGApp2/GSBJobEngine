package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.HouseKeepingService;
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
import java.util.List;
import java.util.Map;

@Component
public class HouseKeepingTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void deleteAppuserHistoryTask() {
//        LOGGER.info("***************************************");
//        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
//        LOGGER.info("Start task1");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("HouseKeepingTask.deleteAppuserHistoryTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("houseKeeping");
            batchTransaction.setStatus("S");
            houseKeepingService.deleteDataByDay();
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
