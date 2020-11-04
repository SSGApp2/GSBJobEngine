package com.app2.engine.controller;

import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/dcms/")
public class DCMSController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DCMSBatchTaskService dcmsBatchTaskService;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    LitigationUpdateService litigationUpdateService;

    @Autowired
    WRNService wrnService;

    @GetMapping("uploadAll")
    public void uploadAll(@RequestParam(value = "date", required = false) String date) {


        // รับข้อมูล Account update ทางคดี และสิ้นสุดคดี (AccountEndLegal) : รับจากระบบ LEAD
        dcmsBatchTaskService.ACN_END_LEGAL(DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_END_LEGAL is completed.");

        // BKC ----------------------------------------------------
        // รับรายละเอียดข้อมูลแฟ้มดำเนินคดีล้มละลายที่มีการ update ในแต่ละวัน : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateBKC();
        LOGGER.debug("Batch : litigationUpdateBKC is completed.");
    }

    @GetMapping("ACNStartLegal")
    public void ACNStartLegal(){
        // ถ้าจะ test บนเครื่อง server เรา ต้องไปเปลี่ยน variable1 เป็น path [ตำแหน่งที่จะเรียกไฟล์] ทั้ง parameter : 5001 ,5002
        String fileName = "ACN_STARTLEGAL_"+ DateUtil.codeCurrentDate()+".txt";
//        smbFileService.remoteFileToLocalFile(fileName,"DCMS");
        ResponseEntity<String> response = dcmsBatchTaskService.ACNStartLegal();
    }

    @GetMapping("ACNEndLegal")
    public void ACNEndLegal(){
        dcmsBatchTaskService.ACN_END_LEGAL(DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_END_LEGAL is completed.");
    }

    @GetMapping("litigationUpdateBKC")
    public void LitigationUpdateBKC(){
        litigationUpdateService.litigationUpdateBKC();
        LOGGER.debug("Batch : litigationUpdateBKC is completed.");
    }

    @GetMapping("litigationUpdateBKO")
    public void litigationUpdateBKO(){
        litigationUpdateService.litigationUpdateBKO();
    }

    @GetMapping("litigationUpdateCVA")
    public void LitigationUpdateCVA(){
        litigationUpdateService.litigationUpdateCVA();
    }

    @GetMapping("litigationUpdateCVC")
    public void LitigationUpdateCVC(){
        litigationUpdateService.litigationUpdateCVC();
    }

    @GetMapping("litigationUpdateCVO")
    public void litigationUpdateCVO(){
        litigationUpdateService.litigationUpdateCVO();
    }

    @GetMapping("WRNConsent")
    public void WRNConsent(){
        wrnService.wrnConsent();
    }

    @GetMapping("wrnTdr")
    public void wrnTdr(){
        wrnService.wrnTDR();
    }

}
