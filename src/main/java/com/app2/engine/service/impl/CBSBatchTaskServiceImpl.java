package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.repository.custom.DocumentRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CBSBatchTaskServiceImpl extends AbstractEngineService implements CBSBatchTaskService {

    private JsonParser parser = new JsonParser();

    private Gson gson = new GsonBuilder().create();

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    DocumentRepositoryCustom documentRepositoryCustom;

    @SneakyThrows
    @Override
    public void LS_COLLECTION_STATUS(String date) {
        FileWriter writer = null;

        //หาข้อมูลจาก database โดยใช้ sql native query ย้ายมากจาก Engine
        List<Map> documentList = documentRepositoryCustom.findDocumentMovementsCollection();

        //หา path local เพื่อสร้างไฟล์ไว้ที่นี่ก่อนแล้วค่อย copy ไปยัง ftp server
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable2(),date);

        String fileName = "LS_COLLECTION_STATUS_" + date + ".txt";

        try {
            File file = new File(path + "/" + fileName);

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            writer = new FileWriter(file, true);
            String BATCH_DATE1 = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            for (Map document : documentList) {

                String CID = (String) document.get("account_no");
                String LEGAL_ID = (String) document.get("doc_number");
                String USER_CODE = (String) document.get("cur_username");

                String BATCH_DATE2 = "";
                if (AppUtil.isNotNull(document.get("updated_date"))) {
                    Date updated_date = sdfDate.parse(document.get("updated_date").toString());
                    BATCH_DATE2 = sdfDate.format(updated_date);
                }

                String docStatus = (String) document.get("doc_status");
                String processStatus = (String) document.get("process_status");
                String adjRedCaseNumber = (String) document.get("adj_red_case_number");
                String adjudication = (String) document.get("adjudication");
                String typeWitness = (String) document.get("type_witness");

                String checkCase = String.valueOf(docStatus) + "#" + String.valueOf(processStatus);
                Map<String, String> mapCase1 = new HashMap<String, String>() {{
                    put("D2#D2-1", "432");      //docStatus + processStatus
                    put("E3#E3-10", "612");     //docStatus + processStatus
                    put("E3#E3-11", "613");     //docStatus + processStatus
                    put("E3#E3-5#1", "513");    //docStatus + processStatus + typeWitness
                    put("E3#E3-5#2", "514");    //docStatus + processStatus + typeWitness
                }};

                String ZCOLLST = "";

                if (mapCase1.containsKey(checkCase)) {
                    ZCOLLST = mapCase1.get(checkCase);
                }
                checkCase = String.valueOf(docStatus) + "#" + String.valueOf(processStatus) + "#" + String.valueOf(typeWitness);
                if (AppUtil.isEmpty(ZCOLLST) && mapCase1.containsKey(checkCase)) {
                    ZCOLLST = mapCase1.get(checkCase);
                }
                if (AppUtil.isEmpty(ZCOLLST) && String.valueOf(docStatus).equals("E3") && String.valueOf(adjudication).equals("A4")) {
                    ZCOLLST = "614";
                }

                if (AppUtil.isEmpty(ZCOLLST) && String.valueOf(docStatus).equals("E3") && AppUtil.isNotEmpty(adjRedCaseNumber) && AppUtil.isNotEmpty(adjudication)) {
                    ZCOLLST = "611";
                }

                if (AppUtil.isEmpty(ZCOLLST)) {
                    Parameter parameter = parameterRepository.findByCode("COLLECTION_STATUS");
                    List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

                    for (ParameterDetail detail : parameterDetails) {
                        if (AppUtil.isNotNull(docStatus) && docStatus.equals(detail.getCode())) {
                            ZCOLLST = detail.getVariable1();
                        } else if (AppUtil.isNotNull(processStatus) && processStatus.equals(detail.getCode())) {
                            ZCOLLST = detail.getVariable1();
                        }
                    }
                }

                if (AppUtil.isNotEmpty(ZCOLLST)) {
                    String line = CID + "|" + BATCH_DATE1 + "|" + BATCH_DATE2 + "|" + ZCOLLST + "|" + LEGAL_ID + "|" + USER_CODE + "\n";
                    writer.write(line);
                    // ข้อมูลที่ออกมาในไฟล์จะไม่มีค่า NULL ต้องเป็นค่าว่างเท่านั้น
                }

            }
            //เมื่อสร้างไฟล์เสร็จแล้ว ต้องปิด connection ทุกครั้ง
            writer.close();
            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(file.getName(), "CBS",date);
        } catch (IOException e) {
            LOGGER.error("Error : {}",e.getMessage(),e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // ignore ... any significant errors should already have been
                    // reported via an IOException from the final flush.
                }
            }
        }
    }

    @Override
    public ResponseEntity<String> accountEndLegalUpdateTask() {
        return getResultByExchange("");
    }

    @Override
    public ResponseEntity<String> createFileTXTRestrictionZLE() {
        String url = "/jobs/createFileTXTRestrictionZLE";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> stblcntryTask(String fileName) {
        String url = "/jobs/stblcntry?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProvinceTask(String fileName) {
        String url = "/jobs/masterDataProvince?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataDistrictTask(String fileName) {
        String url = "/jobs/masterDataDistrict?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataSubDistrictTask(String fileName) {
        String url = "/jobs/masterDataSubDistrict?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataBranchTask(String fileName) {
        String url = "/jobs/masterDataBranch?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataCostCenterTask(String fileName) {
        String url = "/jobs/masterDataCostCenter?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataWorkingDaysTask(String fileName) {
        String url = "/jobs/masterDataWorkingDays?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataHolidayTask(String fileName) {
        String url = "/jobs/masterDataHoliday?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataOUTask(String fileName) {
        String url = "/jobs/masterDataOU?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataMarketCodeTask(String fileName) {
        String url = "/jobs/masterDataMarketCode?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductGroupTask(String fileName) {
        String url = "/jobs/masterDataProductGroup?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductSubtypeTask(String fileName) {
        String url = "/jobs/masterDataProductSubtype?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataProductTypeTask(String fileName) {
        String url = "/jobs/masterDataProductType?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> masterDataTitleTask(String fileName) {
        String url = "/jobs/masterDataTitle?fileName=";
        return getResultByExchange(url + fileName);
    }

    @Override
    public ResponseEntity<String> batchZLETask() {
        String url = "/jobs/batchZLE";
        return getResultByExchange(url);
    }

    @Override
    public ResponseEntity<String> lsAcn() {
        String url = "/jobs/lsACN";
        return getResultByExchange(url);
    }
}
