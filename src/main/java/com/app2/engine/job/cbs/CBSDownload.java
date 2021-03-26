package com.app2.engine.job.cbs;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
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
public class CBSDownload {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?")
    public void LS_ACN() {
        //ส่ง Account Data Synchronization : ส่งให้ระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : LS_ACN_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.LS_ACN");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LS_ACN_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.LS_ACN(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_COUNTRY() {
        // ประเทศ : ส่งให้ระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : STBLCNTRY_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_COUNTRY");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("STBLCNTRY_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_COUNTRY(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

            MASTER_DATA_PROVINCE();

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
    public void MASTER_DATA_PROVINCE() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : STBLCNTRY1_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PROVINCE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("STBLCNTRY1_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_PROVINCE(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

            MASTER_DATA_DISTRICT();
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
    public void MASTER_DATA_DISTRICT() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ZUTBLDIST_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PROVINCE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZUTBLDIST_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_DISTRICT(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

            MASTER_DATA_SUB_DISTRICT();

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
    public void MASTER_DATA_SUB_DISTRICT() {
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ZUTBLSDISTCD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PROVINCE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZUTBLSDISTCD_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_SUB_DISTRICT(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_BRANCH() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLBRCD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_BRANCH");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLBRCD_YYYYMMDD.txt");
        try {
            cbsBatchTaskService.MASTER_DATA_BRANCH(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_COST_CENTER() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLCCNTR_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_COST_CENTER");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLCCNTR_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_COST_CENTER(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_WORKING_DAYS() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLNBD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_WORKING_DAYS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLNBD_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_WORKING_DAYS(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_HOLIDAY() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLNBD1_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_HOLIDAY");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLNBD1_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_HOLIDAY(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?")//ss mm hh every day
    public void MASTER_DATA_OU() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ZUTBLOUBRCD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_OU");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZUTBLOUBRCD_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_OU(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_MARKET_CODE() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : MT_MARKET_CODE_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_MARKET_CODE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("MT_MARKET_CODE_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_MARKET_CODE(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_PRODUCT_GROUP() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : MT_PRODUCT_GROUP_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PRODUCT_GROUP");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("MT_PRODUCT_GROUP_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_PRODUCT_GROUP(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_PRODUCT_SUBTYPE() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : MT_PRODUCT_SUBTYPE_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PRODUCT_SUBTYPE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("MT_PRODUCT_SUBTYPE_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_PRODUCT_SUBTYPE(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_PRODUCT_TYPE() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : MT_PRODUCT_TYPE_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_PRODUCT_TYPE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("MT_PRODUCT_TYPE_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_PRODUCT_TYPE(DateUtil.codeCurrentDate());
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void MASTER_DATA_TITLE() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ZUTBLTITLE2_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_TITLE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZUTBLTITLE2_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.MASTER_DATA_TITLE(DateUtil.codeCurrentDate());
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
