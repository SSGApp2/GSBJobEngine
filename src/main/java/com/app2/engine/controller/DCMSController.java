package com.app2.engine.controller;

import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        dcmsBatchTaskService.ACN_ENDLEGAL(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_ENDLEGAL is completed.");

        dcmsBatchTaskService.ACN_ENDLEGAL_TOTAL(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_ENDLEGAL_TOTAL is completed.");

        // BKC ----------------------------------------------------
        // รับรายละเอียดข้อมูลแฟ้มดำเนินคดีล้มละลายที่มีการ update ในแต่ละวัน : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateBKC(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateBKC is completed.");

        // BKO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกฟ้องลูกหนี้ธนาคารในคดีล้มละลายที่มีการ update : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateBKO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateBKO is completed.");

        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีลูกหนี้ผิดนัดหลังคำพิพากษาที่มีการ update : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateCVA(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVA is completed.");

        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีแพ่งที่มีการ update : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateCVC(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVC is completed.");

        // CVO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกยึดทรัพย์หลักประกันลูกหนี้ธนาคารที่มีการ update ให้ระบบ : รับจากระบบ LEAD
        litigationUpdateService.litigationUpdateCVO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVO is completed.");
    }

    @GetMapping("downloadAll")
    private void downloadAll(@RequestParam(value = "date", required = false) String date,
                             @RequestParam(value = "syncInterface", required = false) String syncInterface) {
        // ส่งข้อมูลบัญชีที่มีรายการแจ้งเตือนกรณีที่ลูกหนี้ที่ศาลมีคำพิพากษาตามยอมทั้งหมด : ส่งให้ระบบ LEAD
        wrnService.WRN_CONSENT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : WRN_CONSENT is completed.");

        // ส่งข้อมูลรายการแจ้งเตือนบัญชีปรับปรุงโครงสร้างหนี้หลังคำพิพากษาผิดนัดชำระหนี้ทั้งหมด : ส่งให้ระบบ LEAD
        wrnService.WRN_TDR(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : WRN_TDR is completed.");

        // ส่งข้อมูล Account และ CIF ที่ต้องการดำเนินคดี (AccountStartLegal)
        dcmsBatchTaskService.ACN_STARTLEGAL(date != null ? date : DateUtil.codeCurrentDate(), syncInterface != null ? syncInterface : "Y");
        LOGGER.debug("Batch : ACN_STARTLEGAL is completed.");
    }

    @GetMapping("ACNStartLegal")
    public void ACNStartLegal(@RequestParam(value = "date", required = false) String date,
                              @RequestParam(value = "syncInterface", required = false) String syncInterface) {
        dcmsBatchTaskService.ACN_STARTLEGAL(date != null ? date : DateUtil.codeCurrentDate(), syncInterface != null ? syncInterface : "Y");
        LOGGER.debug("Batch : ACN_STARTLEGAL is completed.");
    }

    @GetMapping("ACNEndLegal")
    public void ACNEndLegal(@RequestParam(value = "date", required = false) String date) {
        dcmsBatchTaskService.ACN_ENDLEGAL(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_ENDLEGAL is completed.");
    }

    @GetMapping("ACNEndLegalTotal")
    public void ACNEndLegalTotal(@RequestParam(value = "date", required = false) String date) {
        dcmsBatchTaskService.ACN_ENDLEGAL_TOTAL(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : ACN_ENDLEGAL_TOTAL is completed.");
    }

    @GetMapping("litigationUpdateBKC")
    public void LitigationUpdateBKC(@RequestParam(value = "date", required = false) String date) {
        litigationUpdateService.litigationUpdateBKC(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateBKC is completed.");
    }

    @GetMapping("litigationUpdateBKO")
    public void litigationUpdateBKO(@RequestParam(value = "date", required = false) String date) {
        litigationUpdateService.litigationUpdateBKO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateBKO is completed.");
    }

    @GetMapping("litigationUpdateCVA")
    public void LitigationUpdateCVA(@RequestParam(value = "date", required = false) String date) {
        litigationUpdateService.litigationUpdateCVA(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVA is completed.");
    }

    @GetMapping("litigationUpdateCVC")
    public void LitigationUpdateCVC(@RequestParam(value = "date", required = false) String date) {
        litigationUpdateService.litigationUpdateCVC(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVC is completed.");
    }

    @GetMapping("litigationUpdateCVO")
    public void litigationUpdateCVO(@RequestParam(value = "date", required = false) String date) {
        litigationUpdateService.litigationUpdateCVO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : litigationUpdateCVO is completed.");
    }

    @GetMapping("wrnConsent")
    public void wrnConsent(@RequestParam(value = "date", required = false) String date) {
        wrnService.WRN_CONSENT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : WRN_CONSENT is completed.");
    }

    @GetMapping("wrnTdr")
    public void wrnTdr(@RequestParam(value = "date", required = false) String date) {
        wrnService.WRN_TDR(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : WRN_TDR is completed.");
    }

}
