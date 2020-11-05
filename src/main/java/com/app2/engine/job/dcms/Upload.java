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

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Upload {
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

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?") //ss mm hh every day
    public void ACN_END_LEGAL() {
        // รับข้อมูล Account update ทางคดี และสิ้นสุดคดี (AccountEndLegal) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : ACN_ENDLEGAL_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.ACN_END_LEGAL");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ACN_ENDLEGAL_YYYYMMDD.txt");
        try {
            dcmsBatchTaskService.ACN_END_LEGAL(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

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
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKC() {
        // BKC ----------------------------------------------------
        // รับรายละเอียดข้อมูลแฟ้มดำเนินคดีล้มละลายที่มีการ update ในแต่ละวัน : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_BKC_YYYYMMDD.csv");

        BatchTransaction batchTransaction = null;

        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateBKC");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_BKC_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateBKC(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKO() {
        // BKO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกฟ้องลูกหนี้ธนาคารในคดีล้มละลายที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_BKO_YYYYMMDD.csv");

        BatchTransaction batchTransaction = null;

        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateBKO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_BKO_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateBKO(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVA() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีลูกหนี้ผิดนัดหลังคำพิพากษาที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVA_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        try {
            LOGGER.info("File : LitigationUpdate_CVA_YYYYMMDD.csv");
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateCVA");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVA_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVA(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error litigationUpdate_CVA {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVC() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีแพ่งที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVC_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        // CVC ----------------------------------------------------
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateCVC");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVC_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVC(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVO() {
        // CVO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกยึดทรัพย์หลักประกันลูกหนี้ธนาคารที่มีการ update ให้ระบบ : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVO_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMS.Upload.batchLitigationUpdateCVO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVO_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVO(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }
}
