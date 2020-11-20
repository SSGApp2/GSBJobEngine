package com.app2.engine.job.dcms;

import com.app2.engine.entity.app.BatchTransaction;
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
public class DCMSUpload {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DCMSBatchTaskService dcmsBatchTaskService;


    @Autowired
    LitigationUpdateService litigationUpdateService;

    @Autowired
    WRNService wrnService;

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void ACN_ENDLEGAL() {
        // รับข้อมูล Account update ทางคดี และสิ้นสุดคดี (AccountEndLegal) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : ACN_ENDLEGAL_YYYYMMDD.txt");

        dcmsBatchTaskService.ACN_ENDLEGAL(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void ACN_END_LEGAL_TOTAL() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : ACN_ENDLEGAL_TOTAL_YYYYMMDD.txt");

        dcmsBatchTaskService.ACN_ENDLEGAL_TOTAL(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKC() {
        // BKC ----------------------------------------------------
        // รับรายละเอียดข้อมูลแฟ้มดำเนินคดีล้มละลายที่มีการ update ในแต่ละวัน : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_BKC_YYYYMMDD.csv");

        litigationUpdateService.litigationUpdateBKC(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKO() {
        // BKO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกฟ้องลูกหนี้ธนาคารในคดีล้มละลายที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_BKO_YYYYMMDD.csv");

        litigationUpdateService.litigationUpdateBKO(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVA() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีลูกหนี้ผิดนัดหลังคำพิพากษาที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVA_YYYYMMDD.csv");

        litigationUpdateService.litigationUpdateCVA(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVC() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีแพ่งที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVC_YYYYMMDD.csv");

        litigationUpdateService.litigationUpdateCVC(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVO() {
        // CVO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกยึดทรัพย์หลักประกันลูกหนี้ธนาคารที่มีการ update ให้ระบบ : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : LitigationUpdate_CVO_YYYYMMDD.csv");

        litigationUpdateService.litigationUpdateCVO(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }
}
