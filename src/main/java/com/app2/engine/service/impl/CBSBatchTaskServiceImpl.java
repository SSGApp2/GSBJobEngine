package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.CBSRepositoryCustom;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CBSBatchTaskServiceImpl extends AbstractEngineService implements CBSBatchTaskService {

    private JsonParser parser = new JsonParser();

    private Gson gson = new GsonBuilder().create();

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    DebtorAccDebtInfoRepository debtorAccDebtInfoRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    CBSRepositoryCustom cbsRepositoryCustom;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @SneakyThrows
    @Override
    public void LS_COLLECTION_STATUS(String date) {
        ParameterDetail params;
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.LS_COLLECTION_STATUS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LS_COLLECTION_STATUS_YYYYMMDD.txt");

        try {
            //หาข้อมูลจาก database โดยใช้ sql native query ย้ายมากจาก Engine
            List<Map> documentList = cbsRepositoryCustom.findDocumentMovementsCollection();
            Date currentDate = DateUtil.getCurrentDate();

            for (int i = 0; i < 2; i++) {
                //หา path local เพื่อสร้างไฟล์ไว้ที่นี่ก่อนแล้วค่อย copy ไปยัง ftp server
                if (i == 0) {
                    //CBS
                    params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");
                } else {
                    //DCMS
                    params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");
                }
                //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
                String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

                String fileName = "LS_COLLECTION_STATUS_" + date + ".txt";
                int total = 0;
                writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
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
                    String noticDocSendDate = (String) document.get("notic_doc_send_date");   //วันที่ส่งหนังสือบอกกล่าว
                    String lawSuitSendDate = (String) document.get("law_suit_send_date");   //ยื่นคำฟ้องวันที่
                    String examDate = (String) document.get("exam_date");                   //วันที่นัดสืบพยาน
                    String regulatoryDate = (String) document.get("regulatory_date");       //วันที่ออกคำบังคับ
                    String executeDate = (String) document.get("execute_date");          //วันที่ออกหมายบังคับ


                    String ZCOLLST = "";

                    String checkCase = String.valueOf(docStatus) + "#" + String.valueOf(processStatus);
                    Map<String, String> mapCase1 = new HashMap<String, String>() {{
                        put("E3#E3-5#1", "513");    //docStatus + processStatus + typeWitness + exam_date
                        put("E3#E3-5#2", "514");    //docStatus + processStatus + typeWitness + exam_date
                    }};

                    if (checkCase.equals("D2#D2-1") && AppUtil.isNotNull(noticDocSendDate)) {
                        //432-บอกเลิกสัญญาหรือบอกกล่าวบังคับคดี = บันทึก ยื่นคำฟ้องวันที่
                        Date dateLaw = new SimpleDateFormat("dd/MM/yyyy", DateUtil.getSystemLocale()).parse(noticDocSendDate);
                        if (DateUtil.getDateWithRemoveTime(dateLaw).compareTo(DateUtil.getDateWithRemoveTime(currentDate)) <= 0) {
                            ZCOLLST = "432"; //เช็คว่าถึงวันที่กำหนดหรือยัง
                        }
                    }
                    if (checkCase.equals("E3#E3-10") && AppUtil.isNotNull(regulatoryDate)) {
                        //612-ออกคำบังคับ = บันทึกวันที่ออกคำบังคับ
                        ZCOLLST = "612";
                    }
                    if (checkCase.equals("E3#E3-10") && AppUtil.isNotNull(executeDate)) {
                        //613-ออกหมายบังคับ = บันทึกวันที่ออกหมายบังคับ
                        ZCOLLST = "613";
                    }
                    if (mapCase1.containsKey(checkCase)) {
                        ZCOLLST = mapCase1.get(checkCase);
                    }
                    //=========================================================================================================================
                    checkCase = String.valueOf(docStatus) + "#" + String.valueOf(processStatus) + "#" + String.valueOf(typeWitness);
                    if (mapCase1.containsKey(checkCase) && AppUtil.isNotNull(examDate)) {
//                            513-สืบพยานโจทย์ = บันทึกนัดสืบพยานโจทก์+บันทึกนัดสืบพยานวันที่
//                            514-สืบพยานจำเลย = บันทึกนัดสืบพยานจำเลย+บันทึกนัดสืบพยานวันที่
                        ZCOLLST = mapCase1.get(checkCase);
                    }
                    //=========================================================================================================================
                    if (String.valueOf(docStatus).equals("E3") && String.valueOf(adjudication).equals("A4")) { //
                        ZCOLLST = "615";
                    }

                    if (String.valueOf(docStatus).equals("E3") && AppUtil.isNotEmpty(adjRedCaseNumber) && AppUtil.isNotEmpty(adjudication)) {
                        ZCOLLST = "611";
                    }

                    ZCOLLST = mapParameter("COLLECTION_STATUS", docStatus, processStatus);

                    if (AppUtil.isNotEmpty(ZCOLLST) && AppUtil.isNotNull(ZCOLLST)) {
                        String line = CID + "|" + BATCH_DATE1 + "|" + BATCH_DATE2 + "|||" + ZCOLLST + "||" + LEGAL_ID + "|" + USER_CODE + "\n";
                        writer.write(line);
                        // ข้อมูลที่ออกมาในไฟล์จะไม่มีค่า NULL ต้องเป็นค่าว่างเท่านั้น
                        total++;
                    }

                }

                writer.write("Total " + total);
                //เมื่อสร้างไฟล์เสร็จแล้ว ต้องปิด connection ทุกครั้ง
                writer.close();

                //Copy file to FTP Server
                if (i == 0) {
                    smbFileService.localFileToRemoteFile(fileName, "CBS", date);
                } else {
                    smbFileService.localFileToRemoteFile(fileName, "DCMS", date);
                }
            }

            batchTransaction.setStatus("S");
        } catch (IOException e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.error("Error : {}", ex.getMessage(), ex);
                }
            }
        }

    }

    @SneakyThrows
    @Transactional
    public String mapParameter(String parameterCode, String status, String process) {
        Parameter parameter = parameterRepository.findByCode(parameterCode);
        List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

        Map map = new HashMap();
        String mapVariable = null;
        String mapDesc = null;
        for (ParameterDetail detail : parameterDetails) {
            if (AppUtil.isNotNull(status) && status.equals(detail.getCode())) {
                mapVariable = detail.getVariable1();
                mapDesc = detail.getVariable2();
            } else if (AppUtil.isNotNull(process) && process.equals(detail.getCode())) {
                mapVariable = detail.getVariable1();
                mapDesc = detail.getVariable2();
            }
        }

        map.put("mapVariable", mapVariable);
        map.put("mapDesc", mapDesc);

        return mapVariable;
    }

    @Override
    @SneakyThrows
    public void LS_ACCOUNTLIST(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.LS_ACCOUNT_LIST");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LS_ACCOUNTLIST_YYYYMMDD.txt");
        try {
            //หา path local เพื่อสร้างไฟล์ไว้ที่นี่ก่อนแล้วค่อย copy ไปยัง ftp server
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            List<Map> documentList = cbsRepositoryCustom.findLsAccountList();

            String fileName = "LS_ACCOUNTLIST_" + date + ".txt";

            //create new file
            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));
            int total = 0;

            if (!documentList.isEmpty()) {
                total = documentList.size();
                for (Map aDocumentList : documentList) {
                    String CreditAccountNumber = aDocumentList.get("value").toString();
                    ///write data in file
                    writer.write(CreditAccountNumber + "\n");
                }
            }

            writer.write("Total " + total);

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CBS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.error("Error {}", ex.getMessage(), ex);
                }
            }
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_COUNTRY(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "01");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateCountry(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateCountry(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(code);
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable2);
            newParameterDetail.setVariable1(variable3);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_PROVINCE(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "02");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateProvince(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateProvince(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());

            String provinceCd = code + variable1;

            Parameter country = parameterRepository.findByCode("COUNTRY");
            List<ParameterDetail> pCountry = parameterDetailRepository.findByParameter(country);
            List countryDetail = pCountry.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            Parameter province = parameterRepository.findByCode("PROVINCE");
            List<ParameterDetail> pProvince = parameterDetailRepository.findByParameter(province);
            List provinceDetail = pProvince.stream()
                    .filter(value -> value.getCode().equals(provinceCd))
                    .collect(Collectors.toList());

            Parameter parameter = parameterRepository.findByCode(parameterCode);

            ParameterDetail newParameterDetail;
            if (!countryDetail.isEmpty() && provinceDetail.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(provinceCd);
                newParameterDetail.setDescription(variable2);
                newParameterDetail.setDescriptionEng(variable3);
                newParameterDetail.setParameter(parameter);
                parameterDetailRepository.saveAndFlush(newParameterDetail);

            } else if (!countryDetail.isEmpty() && !provinceDetail.isEmpty()) {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, provinceCd);
                newParameterDetail.setCode(provinceCd);
                newParameterDetail.setDescription(variable2);
                newParameterDetail.setDescriptionEng(variable3);
                newParameterDetail.setParameter(parameter);
                parameterDetailRepository.saveAndFlush(newParameterDetail);
            }
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_DISTRICT(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "03");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile() && fileName.equals(fileName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateDistrict(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateDistrict(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());

            String provinceCd = code + variable1;
            String districtCd = code + variable1 + variable2;

            Parameter country = parameterRepository.findByCode("COUNTRY");
            List<ParameterDetail> pCountry = parameterDetailRepository.findByParameter(country);
            List countryDetail = pCountry.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            Parameter province = parameterRepository.findByCode("PROVINCE");
            List<ParameterDetail> pProvince = parameterDetailRepository.findByParameter(province);
            List provinceDetail = pProvince.stream()
                    .filter(value -> value.getCode().equals(provinceCd))
                    .collect(Collectors.toList());

            Parameter district = parameterRepository.findByCode("DISTRICT");
            List<ParameterDetail> pDistrict = parameterDetailRepository.findByParameter(district);
            List districtDetail = pDistrict.stream()
                    .filter(value -> value.getCode().equals(districtCd))
                    .collect(Collectors.toList());

            Parameter parameter = parameterRepository.findByCode(parameterCode);

            ParameterDetail newParameterDetail;
            if (!countryDetail.isEmpty() && !provinceDetail.isEmpty() && districtDetail.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(districtCd);
                newParameterDetail.setDescription(variable3);
                newParameterDetail.setDescriptionEng(variable4);

                newParameterDetail.setParameter(parameter);

                parameterDetailRepository.saveAndFlush(newParameterDetail);
            } else if (!countryDetail.isEmpty() && !provinceDetail.isEmpty() && !districtDetail.isEmpty()) {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, districtCd);
                newParameterDetail.setCode(districtCd);
                newParameterDetail.setDescription(variable3);
                newParameterDetail.setDescriptionEng(variable4);

                newParameterDetail.setParameter(parameter);

                parameterDetailRepository.saveAndFlush(newParameterDetail);
            }
        }
    }

    @Override
    public void MASTER_DATA_SUB_DISTRICT(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "04");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateSubDistrict(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateSubDistrict(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());

            String provinceCd = code + variable1;
            String districtCd = code + variable1 + variable2;
            String subDistrictCd = code + variable1 + variable2 + variable3;

            Parameter country = parameterRepository.findByCode("COUNTRY");
            List<ParameterDetail> pCountry = parameterDetailRepository.findByParameter(country);
            List countryDetail = pCountry.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            Parameter province = parameterRepository.findByCode("PROVINCE");
            List<ParameterDetail> pProvince = parameterDetailRepository.findByParameter(province);
            List provinceDetail = pProvince.stream()
                    .filter(value -> value.getCode().equals(provinceCd))
                    .collect(Collectors.toList());

            Parameter district = parameterRepository.findByCode("DISTRICT");
            List<ParameterDetail> pDistrict = parameterDetailRepository.findByParameter(district);
            List districtDetail = pDistrict.stream()
                    .filter(value -> value.getCode().equals(districtCd))
                    .collect(Collectors.toList());

            Parameter subDistrict = parameterRepository.findByCode("SUBDISTRICT");
            List<ParameterDetail> pSubDistrict = parameterDetailRepository.findByParameter(subDistrict);
            List subDistrictDetail = pSubDistrict.stream()
                    .filter(value -> value.getCode().equals(subDistrictCd))
                    .collect(Collectors.toList());

            Parameter parameter = parameterRepository.findByCode(parameterCode);

            ParameterDetail newParameterDetail;
            if (!countryDetail.isEmpty() && !provinceDetail.isEmpty() && !districtDetail.isEmpty() && subDistrictDetail.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(subDistrictCd);
                newParameterDetail.setDescription(variable4);
                newParameterDetail.setDescriptionEng(variable5);

                newParameterDetail.setParameter(parameter);

                parameterDetailRepository.saveAndFlush(newParameterDetail);

            } else if (!countryDetail.isEmpty() && !provinceDetail.isEmpty() && !districtDetail.isEmpty() && !subDistrictDetail.isEmpty()) {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, subDistrictCd);
                newParameterDetail.setCode(subDistrictCd);
                newParameterDetail.setDescription(variable4);
                newParameterDetail.setDescriptionEng(variable5);

                newParameterDetail.setParameter(parameter);

                parameterDetailRepository.saveAndFlush(newParameterDetail);
            }
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_BRANCH(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "05");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {

                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateBranch(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateBranch(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }
            newParameterDetail.setCode(code);
            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable3);
            newParameterDetail.setVariable1(variable2);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);
            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_COST_CENTER(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "06");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateCostCenter(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateCostCenter(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setCode(code);
            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable2);
            newParameterDetail.setVariable1(variable3);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_WORKING_DAYS(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "07");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateWorkingDays(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateWorkingDays(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setCode(code);
            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable2);
            newParameterDetail.setVariable1(variable3);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_HOLIDAY(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "08");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable0" + i, data[i]);
                        }
                    }
                    if (map.size() > 2) {
                        String dateStr = map.get("variable01").toString();
                        String[] dateSp = dateStr.split("/");
                        if (dateSp.length > 2) {
                            for (int i = 0; i <= (dateSp.length - 1); i++) {
                                map.put("variable" + i, dateSp[i]);
                            }
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                Parameter holiday = parameterRepository.findByCode(parameterCode);
                List<ParameterDetail> pHoliday = parameterDetailRepository.findByParameter(holiday);
                parameterDetailRepository.deleteAll(pHoliday);

                saveOrUpdateHoliday(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateHoliday(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable = (map.get("variable01") == null ? null : map.get("variable01").toString());
            String variable0 = (map.get("variable0") == null ? null : map.get("variable0").toString());
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());

            String holidayCd = code + variable0 + variable1 + variable2;

            Parameter parameter = parameterRepository.findByCode(parameterCode);

            ParameterDetail newParameterDetail = new ParameterDetail();
            newParameterDetail.setCode(holidayCd);
            newParameterDetail.setName(code);
            newParameterDetail.setDescription(variable);
            newParameterDetail.setVariable1(variable0);
            newParameterDetail.setVariable2(variable1);
            newParameterDetail.setVariable3(variable2);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);

        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_OU(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "09");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค File วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateOU(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateOU(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(code);
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setVariable1(variable1);
            newParameterDetail.setVariable2(variable2);
            newParameterDetail.setVariable3(variable3);
            newParameterDetail.setVariable4(variable4);
            newParameterDetail.setVariable5(variable5);
            newParameterDetail.setVariable6(variable6);
            newParameterDetail.setVariable7(variable7);
            newParameterDetail.setVariable8(variable8);
            newParameterDetail.setVariable9(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_MARKET_CODE(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "10");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;
        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateMarketCode(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateMarketCode(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            if (AppUtil.isNotNull(variable1)) {
                Parameter parameter = parameterRepository.findByCode(parameterCode);
                List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

                List detailCode = parameterDetails.stream()
                        .filter(value -> value.getCode().equals(code))
                        .collect(Collectors.toList());

                ParameterDetail newParameterDetail;
                if (detailCode.isEmpty()) {
                    newParameterDetail = new ParameterDetail();
                    newParameterDetail.setCode(code);
                } else {
                    newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
                }

                newParameterDetail.setDescription(variable1);
                newParameterDetail.setDescriptionEng(variable2);
                newParameterDetail.setVariable1(variable3);
                newParameterDetail.setVariable2(variable4);
                newParameterDetail.setVariable3(variable5);
                newParameterDetail.setVariable4(variable6);
                newParameterDetail.setVariable5(variable7);
                newParameterDetail.setVariable6(variable8);
                newParameterDetail.setVariable7(variable9);

                newParameterDetail.setParameter(parameter);

                parameterDetailRepository.saveAndFlush(newParameterDetail);
            }
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_PRODUCT_GROUP(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "11");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateProductGroup(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateProductGroup(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(code);
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable2);
            newParameterDetail.setVariable1(variable3);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_PRODUCT_SUBTYPE(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "12");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateProductSubtype(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateProductSubtype(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
                newParameterDetail.setCode(code);
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setDescription(variable1);
            newParameterDetail.setDescriptionEng(variable2);
            newParameterDetail.setVariable1(variable3);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_PRODUCT_TYPE(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "13");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateProductType(dataList);

            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateProductType(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());
            String variable5 = (map.get("variable5") == null ? null : map.get("variable5").toString());
            String variable6 = (map.get("variable6") == null ? null : map.get("variable6").toString());
            String variable7 = (map.get("variable7") == null ? null : map.get("variable7").toString());
            String variable8 = (map.get("variable8") == null ? null : map.get("variable8").toString());
            String variable9 = (map.get("variable9") == null ? null : map.get("variable9").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setCode(code);
            newParameterDetail.setDescription(variable2);
            newParameterDetail.setDescriptionEng(variable3);
            newParameterDetail.setVariable1(variable1);
            newParameterDetail.setVariable2(variable4);
            newParameterDetail.setVariable3(variable5);
            newParameterDetail.setVariable4(variable6);
            newParameterDetail.setVariable5(variable7);
            newParameterDetail.setVariable6(variable8);
            newParameterDetail.setVariable7(variable9);

            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public void MASTER_DATA_TITLE(String date) {
        ParameterDetail parameterDetails = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "16");
        String fileName = parameterDetails.getVariable1() + date + ".txt";
        String parameterCode = parameterDetails.getVariable2();

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);

            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                List<Map> list = new ArrayList<>();
                List<Map> dataList = new ArrayList<>();
                String readLine;

                while ((readLine = reader.readLine()) != null) {
                    String[] data = readLine.split("\\|");

                    Map map = new HashMap();
                    for (int i = 0; i <= (data.length - 1); i++) {
                        if (i == 0) {
                            map.put("parameterCode", parameterCode);
                            map.put("code", data[i]);
                        } else {
                            map.put("variable" + i, data[i]);
                        }
                    }
                    list.add(map);
                }

                for (int i = 1; i <= list.size() - 2; i++) {///// ตัดบรรทัดแรกที่เป็น DESC และบรรทัดสุดท้ายที่เป็น Total
                    dataList.add(list.get(i));
                }

                saveOrUpdateTitle(dataList);
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    @SneakyThrows
    public void saveOrUpdateTitle(List<Map> data) {
        for (Map map : data) {
            String parameterCode = map.get("parameterCode").toString();
            String code = map.get("code").toString();
            String variable1 = (map.get("variable1") == null ? null : map.get("variable1").toString());
            String variable2 = (map.get("variable2") == null ? null : map.get("variable2").toString());
            String variable3 = (map.get("variable3") == null ? null : map.get("variable3").toString());
            String variable4 = (map.get("variable4") == null ? null : map.get("variable4").toString());

            Parameter parameter = parameterRepository.findByCode(parameterCode);
            List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);

            List detailCode = parameterDetails.stream()
                    .filter(value -> value.getCode().equals(code))
                    .collect(Collectors.toList());

            ParameterDetail newParameterDetail;
            if (detailCode.isEmpty()) {
                newParameterDetail = new ParameterDetail();
            } else {
                newParameterDetail = parameterDetailRepository.findByPCodeAndPdCode(parameterCode, code);
            }

            newParameterDetail.setCode(code);
            if (variable1 != null && variable1.equals("0")) {///desc TH
                newParameterDetail.setDescription(variable2);
                newParameterDetail.setVariable1(variable3);
                newParameterDetail.setVariable2(variable4);
            } else {///desc EN
                newParameterDetail.setDescriptionEng(variable2);
                newParameterDetail.setVariable3(variable3);
                newParameterDetail.setVariable4(variable4);
            }
            newParameterDetail.setParameter(parameter);

            parameterDetailRepository.saveAndFlush(newParameterDetail);
        }
    }

    @SneakyThrows
    @Override
    public void ZLE(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.ZLE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZLE_YYYYMMDD.txt");

        try {
            //หา path local เพื่อสร้างไฟล์ไว้ที่นี่ก่อนแล้วค่อย copy ไปยัง ftp server
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            List<Map> debtorList = new ArrayList<>();

            String fileName = "ZLE_" + date + ".txt";

            //create new file
            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));
            String total = "0";


            writer.write("ID|CUST_TYPE|RESTRICTION\n");

            if (!debtorList.isEmpty()) {
                total = String.valueOf(debtorList.size());
                for (Map debtor : debtorList) {
                    String type_person = (debtor.get("type_person") == null ? "null" : debtor.get("type_person").toString());
                    String card_number = (debtor.get("card_number") == null ? "null" : debtor.get("card_number").toString());
                    String restriction = "";

                    if (!type_person.equals("null") && !card_number.equals("null")) {
                        ///write data in file
                        writer.write(card_number + "|" + type_person + "|" + restriction + "\n");
                    }
                }
            }

            writer.write("Total " + total + "|\n");

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CBS", date);
            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.error("Error {}", ex.getMessage(), ex);
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void LS_ACN(String date) {
        String fileName = "LS_ACN_" + date + ".txt";

        smbFileService.remoteFileToLocalFile(fileName, "CBS", date);

        //หา path local Download
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "02");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

        BufferedReader reader = null;

        try {
            File file = new File(path + "/" + fileName);
            if (file.exists() && !file.isDirectory()) {
                List<String> linesList = new ArrayList<>();
                Scanner fileReader = new Scanner(file);

                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                String readLine;
                while ((readLine = reader.readLine()) != null) {
                    linesList.add(readLine);
                }

                for (int i = 0; i <= linesList.size() - 2; i++) {
                    if (i == 0) continue;
                    String row = linesList.get(i);
                    if (!row.equals("")) {
                        String[] resultSplitAr = row.split("\\|");

                        if (resultSplitAr.length > 0) {
                            String ACN = resultSplitAr[0];
                            String BRANCH_CD = resultSplitAr[1];
                            String ACCT_STATUS = resultSplitAr[2];
                            String ACCT_STATUS_DESC = resultSplitAr[3];
                            String PRODUCT_CLASS = resultSplitAr[4];
                            String PRODUCT_GRP = resultSplitAr[5];
                            String PRODUCT_GRP_DESC = resultSplitAr[6];
                            String MARKET_CD = resultSplitAr[7];
                            String MARKET_CD_DESC = resultSplitAr[8];
                            String ACCT_RELINFO = resultSplitAr[9];
                            String ACCT_RELINFO_DESC = resultSplitAr[10];
                            String LEDGER_BALANCE = resultSplitAr[11];
                            String AVAILABLE_BALANCE = resultSplitAr[12];
                            String LIMIT_AMOUNT = resultSplitAr[13];
                            String OUTSTANDING_BALANCE = resultSplitAr[14];
                            String INTEREST_RECEIVABLE = resultSplitAr[15];
                            String LATE_CHARGE_DUE = resultSplitAr[16];
                            String TDU_AMOUNT = resultSplitAr[17];
                            String SCHEDULED_INTERNALBILL = resultSplitAr[18];
                            String INTERNALBILL_NEXTDUE = resultSplitAr[19];
                            String LAST_PAID_DT = resultSplitAr[20];
                            String WRITE_OFF_FLG = resultSplitAr[21];
                            String WRITE_OFF_DESC = resultSplitAr[22];
                            String TDR_FLG = resultSplitAr[23];
                            String LEGAL_STATUS = resultSplitAr[24];
                            String LEGAL_DESC = resultSplitAr[25];
                            String PRODUCT_SUBTYPE = resultSplitAr[26];
                            String PRODUCT_SUBTYPE_DESC = resultSplitAr[27];
                            String COLLECTION_STATUS_CD = resultSplitAr[28];
                            String COLLECTION_STATUS_DESC = resultSplitAr[29];
                            String COMMITMENT_ACCT = resultSplitAr[30];
                            String RESTRUCTURED_ACCT = resultSplitAr[31];
                            String ADVANCEMENT_ACCT = resultSplitAr[32];
                            String PRODUCT_TYPE = resultSplitAr[33];
                            String PRODUCT_TYPE_DESC = resultSplitAr[34];
                            String IS_FLATRATE = resultSplitAr[35];
                            String MONTH_OVERDUE = resultSplitAr[36];
                            String PROV_CAT = resultSplitAr[37];
                            String PROV_CAT_DESC = resultSplitAr[38];

                            DebtorAccDebtInfo debtorAccDebtInfo = debtorAccDebtInfoRepository.findByAccountNo(ACN);

                            if (AppUtil.isNotNull(debtorAccDebtInfo)) {

                                debtorAccDebtInfo.setAccountNo(AppUtil.checkEmptyStr(ACN));
                                if (!BRANCH_CD.equals("")) {
                                    Long branchID = Long.valueOf(BRANCH_CD);
                                    Branch branch = branchRepository.getOne(branchID);
                                    if (AppUtil.isNotNull(branch)) {
                                        debtorAccDebtInfo.setBranchAccount(branch.getCode());
                                    }
                                }
                                debtorAccDebtInfo.setDocCreatedStatus(AppUtil.checkEmptyStr(ACCT_STATUS));
                                debtorAccDebtInfo.setAcctStatusDesc(AppUtil.checkEmptyStr(ACCT_STATUS_DESC));
                                debtorAccDebtInfo.setProductClass(AppUtil.checkEmptyStr(PRODUCT_CLASS));
                                debtorAccDebtInfo.setProductGroup(AppUtil.checkEmptyStr(PRODUCT_GRP));
                                debtorAccDebtInfo.setMarketCode(AppUtil.checkEmptyStr(MARKET_CD));
                                debtorAccDebtInfo.setMarketDesc(AppUtil.checkEmptyStr(MARKET_CD_DESC));
                                if (!LAST_PAID_DT.equals("")) {
                                    Date LastPaidDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(LAST_PAID_DT);
                                    debtorAccDebtInfo.setLastPaidDate(LastPaidDate);
                                }
                                debtorAccDebtInfo.setWriteOffFlag(AppUtil.checkEmptyStr(WRITE_OFF_FLG));
                                debtorAccDebtInfo.setTdrFlag(AppUtil.checkEmptyStr(TDR_FLG));
                                debtorAccDebtInfo.setLegalStatus(AppUtil.checkEmptyStr(LEGAL_STATUS));
                                debtorAccDebtInfo.setLegalStatusDesc(AppUtil.checkEmptyStr(LEGAL_DESC));
                                debtorAccDebtInfo.setCollectionStatus(AppUtil.checkEmptyStr(COLLECTION_STATUS_CD));
                                debtorAccDebtInfo.setCollectionStatusDesc(AppUtil.checkEmptyStr(COLLECTION_STATUS_DESC));
                                debtorAccDebtInfo.setProductSubtypeCode(AppUtil.checkEmptyStr(PRODUCT_SUBTYPE));
                                debtorAccDebtInfo.setProductSubtypeDesc(AppUtil.checkEmptyStr(PRODUCT_SUBTYPE_DESC));
                                LOGGER.info("accountNo : {}", debtorAccDebtInfo.getAccountNo());
                                debtorAccDebtInfoRepository.saveAndFlush(debtorAccDebtInfo);
                            }
                        }
                    }
                }
                fileReader.close();
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            LOGGER.error("Error save file {}", fileName);
            throw new RuntimeException(fileName);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }
}
