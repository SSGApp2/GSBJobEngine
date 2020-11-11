package com.app2.engine.job.dcms;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
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
public class DCMSDownload {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DCMSBatchTaskService dcmsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    LitigationUpdateService litigationUpdateService;

    @Autowired
    WRNService wrnService;

    @Transactional ///ยังแก้ไม่เสร็จ
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void ACN_START_LEGAL() {
        // ส่งข้อมูล Account และ CIF ที่ต้องการดำเนินคดี (AccountStartLegal)
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ACN_STARTLEGAL_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Download.ACN_START_LEGAL");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ACN_STARTLEGAL_YYYYMMDD.txt");
        try {
            dcmsBatchTaskService.ACNStartLegal();
            batchTransaction.setStatus("S");

        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") //ss mm hh every day
    public void WRN_CONSENT() {
        // ส่งข้อมูลบัญชีที่มีรายการแจ้งเตือนกรณีที่ลูกหนี้ที่ศาลมีคำพิพากษาตามยอมทั้งหมด : ส่งให้ระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : WRN_CONSENT_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Download.WRN_CONSENT");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("WRN_CONSENT_YYYYMMDD.txt");

        try {
            wrnService.WRN_CONSENT(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") //ss mm hh every day
    public void WRN_TDR() {
        // ส่งข้อมูลรายการแจ้งเตือนบัญชีปรับปรุงโครงสร้างหนี้หลังคำพิพากษาผิดนัดชำระหนี้ทั้งหมด : ส่งให้ระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : WRN_TDR_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Download.WRN_TDR");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("WRN_TDR_YYYYMMDD.txt");

        try {
            wrnService.WRN_TDR(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        } catch (Exception e) {
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
