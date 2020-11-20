package com.app2.engine.service.impl;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.custom.DCMSRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class DCMSBatchTaskServiceImpl extends AbstractEngineService implements DCMSBatchTaskService {
    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    DCMSRepositoryCustom dcmsRepositoryCustom;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Override
    public void ACN_STARTLEGAL(String date,String syncInterface) {

        BufferedReader reader = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.ACN_STARTLEGAL");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ACN_STARTLEGAL_YYYYMMDD.txt");
        try {

            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");
            String path = FileUtil.isNotExistsDirCreated(params.getVariable1(), date);

            String filename = "ACN_STARTLEGAL_" + date + ".txt";

            smbFileService.remoteFileToLocalFile(filename,"DCMS",date);

            File file = new File(path + "/" + filename);

            if (file.exists() && !file.isDirectory()) {

                List<String> rowList = new ArrayList<>();
                String readLine;

                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "TIS-620"));

                while ((readLine = reader.readLine()) != null) {
                    rowList.add(readLine);
                }

                for (int i = 0; i <= rowList.size() - 2; i++) {
                    if (i == 0) continue;
                    String row = rowList.get(i);

                    if (!row.equals("")) {
                        String[] resultSplitAr = row.split("\\|");

                        if (resultSplitAr.length > 0) {
                            String cifNo = resultSplitAr[0];
                            String title = resultSplitAr[1];
                            String fname = resultSplitAr[2];
                            String lname = resultSplitAr[3];
                            String accountNo = resultSplitAr[4];
                            String accountBrcd = resultSplitAr[5];
                            String sendDtStr = resultSplitAr[6];
                            String login = resultSplitAr[7];
                            String fistName = resultSplitAr[8];
                            String lastName = resultSplitAr[9];
                            String custType = resultSplitAr[10];

                            JSONObject jsonToCreateDoc = new JSONObject();
                            JSONObject debtorJson = new JSONObject().put("cif", cifNo).put("person", "");
                            JSONObject documentJson = new JSONObject().put("docType", "1").put("docStatus", "A1").put("docCreateStatus", "A");

                            List list = new ArrayList();
                            list.add(accountNo);

                            JSONObject debtorAccDebtInfoJson = new JSONObject();
                            debtorAccDebtInfoJson.put("accountNo", list);

                            jsonToCreateDoc.put("debtor", debtorJson);
                            jsonToCreateDoc.put("document", documentJson);
                            jsonToCreateDoc.put("debtorAccDebtInfo", debtorAccDebtInfoJson);
                            jsonToCreateDoc.put("role", "");
                            jsonToCreateDoc.put("interface", syncInterface);
                            jsonToCreateDoc.put("flag", "B");
                            jsonToCreateDoc.put("branchCenter", accountBrcd);
                            jsonToCreateDoc.put("sueDate", sendDtStr);
                            jsonToCreateDoc.put("userSend", login);
                            jsonToCreateDoc.put("userSendName", fistName);
                            jsonToCreateDoc.put("userSendLastName", lastName);
                            postWithJsonCustom(jsonToCreateDoc.toString(), HttpMethod.POST, "/document/createCaseFile");
                        }
                    }
                }

                reader.close();
            }

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());

            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.error("Error : {}", ex.getMessage(), ex);
                }
            }
        }

    }

    @SneakyThrows
    @Override
    public void ACN_ENDLEGAL(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.ACN_END_LEGAL");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ACN_ENDLEGAL_YYYYMMDD.txt");

        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            String fileName = "ACN_ENDLEGAL_" + date + ".txt";
            LOGGER.debug("fileName : {} ", fileName);

            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

            for (Map debtorAccDebtInfo : dcmsRepositoryCustom.findAcnEndLegal()) {
                BigInteger accountNo = null;
                String wfTypeID = "";
                String wfTypeDesc = "";
                String endDt = "";
                String endStepReasonID = "";
                String endStepReasonDesc = "";
                String description = "";

                accountNo = new BigInteger(String.valueOf(debtorAccDebtInfo.get("accountNo")));

                if (AppUtil.isNotNull(debtorAccDebtInfo.get("docType"))) {
                    String docType = String.valueOf(debtorAccDebtInfo.get("docType"));

                    switch (docType) {
                        case "1":
                            wfTypeID = "1";
                            break;
                        case "2":
                            wfTypeID = "3";
                            break;
                        case "3":
                            wfTypeID = "4";
                            break;
                        case "4":
                            wfTypeID = "5";
                            break;
                        case "5":
                            wfTypeID = "6";
                            break;
                    }
                }

                if (AppUtil.isNotNull(accountNo)) {
                    String dataStr = accountNo + "|" + wfTypeID + "|" + wfTypeDesc + "|" + endDt + "|" + endStepReasonID + "|" + endStepReasonDesc + "|" + description;
                    writer.write(dataStr);
                    writer.newLine();
                }

            }

            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());

            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
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
    @Override
    public void ACN_ENDLEGAL_TOTAL(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.ACN_END_LEGAL_TOTAL");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ACN_ENDLEGAL_TOTAL_YYYYMMDD.txt");

        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            String fileName = "ACN_ENDLEGAL_TOTAL_" + date + ".txt";
            LOGGER.debug("fileName : {} ", fileName);

            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

            List<Map> debtorAccDebtInfoList = dcmsRepositoryCustom.findAcnEndLegal();
            writer.write(String.valueOf(debtorAccDebtInfoList.size()));
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
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
}


