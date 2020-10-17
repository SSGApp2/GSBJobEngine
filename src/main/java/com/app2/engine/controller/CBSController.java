package com.app2.engine.controller;

import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/api/jobs/cbs/")
public class CBSController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @GetMapping("lsCollectionStatus")
    public void lsCollectionStatus(){
        ResponseEntity<String> response = cbsBatchTaskService.lsCollectionStatusTask();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CBS");
    }

    @GetMapping("zle")
    public void zle(){
        ResponseEntity<String> response = cbsBatchTaskService.batchZLETask();
        String fileName = response.getBody();
        smbFileService.localFileToRemoteFile(fileName,"CBS");
    }

    @GetMapping("lsAcn")
    public void lsAcnTask(){
        String fileName = "LS_ACN_"+codeCurrentDate()+".txt";
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.lsAcn();
    }

    @GetMapping("stblcntry")
    public void stblcntryTask(){
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "01");
        String fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        cbsBatchTaskService.stblcntryTask(fileName);
    }

    @GetMapping("province")
    public void masterDataProvinceTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "02");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataProvinceTask(fileName);
    }

    @GetMapping("district")
    public void masterDataDistrictTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "03");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataDistrictTask(fileName);
    }

    @GetMapping("ditrictCd")
    public void masterDataDistrictCdTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "04");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataSubDistrictTask(fileName);
    }

    @GetMapping("utblBrcd")
    public void masterDataBranchTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "05");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataBranchTask(fileName);
    }

    @GetMapping("utblcCntr")
    public void masterDataCostCenterTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "06");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataCostCenterTask(fileName);
    }

    @GetMapping("utblNbd")
    public void masterDataWorkingDaysTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "07");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataWorkingDaysTask(fileName);
    }

    @GetMapping("utblNbd1")
    public void masterDataHolidayTask(){
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "08");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataHolidayTask(fileName);
    }
    @GetMapping("zutblOuBrcd")
    public void masterDataOUTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "09");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataOUTask(fileName);
    }

    @GetMapping("mtMarketCode")
    public void masterDataMarketCodeTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "10");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataMarketCodeTask(fileName);
    }

    @GetMapping("mtProductGroup")
    public void masterDataProductGroupTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "11");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataProductGroupTask(fileName);
    }

    @GetMapping("mtProductSubType")
    public void masterDataProductSubtypeTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "12");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataProductSubtypeTask(fileName);
    }

    @GetMapping("mtProductType")
    public void masterDataProductTypeTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "13");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataProductTypeTask(fileName);
    }

    @GetMapping("zutblTitle")
    public void masterDataTitleTask() {
        String fileName = null;
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String today = timeLog + ".txt";
        ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "16");
        fileName = file.getVariable1() + today;
        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.masterDataTitleTask(fileName);
    }

    public String codeCurrentDate(){
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0]+currentDateAr[1]+currentDateAr[2];
        return codeDate;
    }
}
