package com.app2.engine.controller;

import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public String codeCurrentDate(){
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0]+currentDateAr[1]+currentDateAr[2];
        return codeDate;
    }

    @GetMapping("ACNStartLegal")
    public void ACNStartLegal(){
        // ถ้าจะ test บนเครื่อง server เรา ต้องไปเปลี่ยน variable1 เป็น path [ตำแหน่งที่จะเรียกไฟล์] ทั้ง parameter : 5001 ,5002
        String fileName = "ACN_STARTLEGAL_"+codeCurrentDate()+".txt";
        smbFileService.remoteFileToLocalFile(fileName,"DCMS");
        ResponseEntity<String> response = dcmsBatchTaskService.ACNStartLegal();
    }

    @GetMapping("ACNEndLegal")
    public void ACNEndLegal(){
        ResponseEntity<String> response = dcmsBatchTaskService.ACNEndLegal();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"DCMS");
    }

    @GetMapping("litigationUpdateBKC")
    public void LitigationUpdateBKC(){
        litigationUpdateService.litigationUpdateBKC();
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
