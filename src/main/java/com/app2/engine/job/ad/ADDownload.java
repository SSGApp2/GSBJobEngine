package com.app2.engine.job.ad;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.EmployeeADService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ADDownload {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    EmployeeADService employeeADService;

    @Transactional
    @Scheduled(cron = "0 30 5 * * ?")
    public void InsertOrUpdateEmp(){
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : AD_YYYYMMDD.CSV");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("AD.Download.InsertOrUpdateEmp");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("AD_YYYYMMDD.CSV");

        try {
            employeeADService.InsertOrUpdateEmp(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");
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
