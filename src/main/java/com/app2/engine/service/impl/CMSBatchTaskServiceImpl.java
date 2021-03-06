package com.app2.engine.service.impl;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.custom.CMSRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CMSBatchTaskServiceImpl extends AbstractEngineService implements CMSBatchTaskService {

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    CMSRepositoryCustom cmsRepositoryCustom;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Override
    @SneakyThrows
    public void SEIZE_INFO(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.SEIZE_INFO");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("SEIZEINFO_YYYYMMDD.txt");

        String fileName = "SEIZEINFO_" + date + ".txt";
        try {
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));


            List<Map> guaranteeList = cmsRepositoryCustom.findSeizeInfoGuarantee();
            int total = guaranteeList.size();

            for (Map guaranteeMap : guaranteeList) {
                String collID = "";
                String isSeizeCancel = "0";
                String seizeDecidedCaseNo = "";
                String seizeDate = "";
                String seizeBy = "";
                String seizeJudgeDate = "";
                String seizeLawCourt = "";
                String seizeComplaintant ="";
                String seizeExecution = "";
                String seizeCancelDate = "";
                String isSeize = "";

                if (AppUtil.isNotNull(guaranteeMap.get("docNumber"))) {
                    collID = guaranteeMap.get("docNumber").toString();
                }

                if (AppUtil.isNotNull(guaranteeMap.get("document"))) {
                    String document = guaranteeMap.get("document").toString();
                    List<Map> documentProgressList = cmsRepositoryCustom.findSeizeInfoDocProgress(document);

                    if (!documentProgressList.isEmpty()) { ///documentProgress เรียงลำดับวันที่อัพเดตล่าสุด
                        if (AppUtil.isNotNull(documentProgressList.get(0).get("redCaseNumber"))
                                && AppUtil.isNotNull(documentProgressList.get(0).get("blackCaseNumber"))) {
                            seizeDecidedCaseNo = documentProgressList.get(0).get("redCaseNumber").toString();
                        } else if (AppUtil.isNotNull(documentProgressList.get(0).get("redCaseNumber"))) {
                            seizeDecidedCaseNo = documentProgressList.get(0).get("redCaseNumber").toString();
                        } else if (AppUtil.isNotNull(documentProgressList.get(0).get("blackCaseNumber"))) {
                            seizeDecidedCaseNo = documentProgressList.get(0).get("blackCaseNumber").toString();
                        }
                        if(AppUtil.isNotNull(documentProgressList.get(0).get("adjDate"))){
                            seizeJudgeDate = documentProgressList.get(0).get("adjDate").toString();
                        }
                        if(AppUtil.isNotNull(documentProgressList.get(0).get("courtName"))){
                            seizeLawCourt = documentProgressList.get(0).get("courtName").toString();
                        }
                    }
                }

                String guarantee = guaranteeMap.get("guaranteeID").toString();
                List<Map> confiscateList = cmsRepositoryCustom.findSeizeInfoConfiscate(guarantee);

                if (!confiscateList.isEmpty()) { ///confiscate เรียงลำดับวันที่อัพเดตล่าสุด
                    if (AppUtil.isNotNull(confiscateList.get(0).get("confiscateDate"))) {
                        seizeDate = DateUtil.convertStringDateTimeToString(confiscateList.get(0).get("confiscateDate").toString());
                    }
                    if (AppUtil.isNotNull(confiscateList.get(0).get("courtAdjudicate"))) {
                        seizeExecution = confiscateList.get(0).get("courtAdjudicate").toString();
                    }
                    if (AppUtil.isNotNull(confiscateList.get(0).get("seizeComplainTant"))) {
                        seizeComplaintant = confiscateList.get(0).get("seizeComplainTant").toString();
                    }
                    if (AppUtil.isNotNull(confiscateList.get(0).get("seizeCancelDate"))) {
                        seizeCancelDate = confiscateList.get(0).get("seizeCancelDate").toString();
                    }
                    if (AppUtil.isNotNull(confiscateList.get(0).get("isSeize"))) {
                        isSeize = confiscateList.get(0).get("isSeize").toString();
                    }
                }


//                String dataStr = collID + "|" + isSeizeCancel + "|" + seizeDecidedCaseNo + "|" + seizeDate + "||" + seizeLawCourt + "|" + seizeJudgeDate + "||" + seizeExecution + "|||"; version เก่า
                String dataStr = collID + "|" + isSeizeCancel + "|" + seizeDecidedCaseNo + "|" + seizeDate + "|"+seizeBy+"|" + seizeJudgeDate + "|" + seizeLawCourt + "|"+seizeComplaintant+"|"+seizeExecution+"|"+seizeCancelDate+"|"+isSeize;
                writer.write(dataStr);
                writer.newLine();
            }
            writer.write("Total " + total + "\n");
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CMS", date);

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

    @Override
    @SneakyThrows
    public void LEGALSTATUS(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.LEGAL_STATUS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LEGALSTATUS_YYYMMDD.txt");

        String fileName = "LEGALSTATUS_" + date + ".txt";

        try {
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);
            String dataHeaderStr = "collID|legalID|legalStatusCode|legalStatusDesc|";

            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));


            writer.write(dataHeaderStr);
            writer.newLine();

            List<Map> guaranteeInfoList = cmsRepositoryCustom.findLegalStatusGuarantee();

            for (Map guaranteeMap : guaranteeInfoList) {
                String resultStatus = processLegalStatusCode(guaranteeMap);
                String collID = "";
                String legalID = ""; //ยังไม่มี
                String legalStatusCode = "";
                String legalStatusDesc = "";

                if (AppUtil.isNotNull(guaranteeMap.get("docNumber"))) {
                    collID = guaranteeMap.get("docNumber").toString();
                }

                if (AppUtil.isNotNull(resultStatus)) {
                    if (resultStatus.equals("case0")) {
                        legalStatusCode = "0";
                        legalStatusDesc = "ไม่มี Desc";
                    } else if (resultStatus.equals("case1")) {
                        legalStatusCode = "1";
                        legalStatusDesc = "ส่งดำเนินคดี";
                    } else if (resultStatus.equals("case2")) {
                        legalStatusCode = "2";
                        legalStatusDesc = "ยื่นฟ้องดำเนินคดี";
                    } else if (resultStatus.equals("case3")) {
                        legalStatusCode = "3";
                        legalStatusDesc = "พิพากษา";
                    } else if (resultStatus.equals("case4")) {
                        legalStatusCode = "4";
                        legalStatusDesc = "ยึดทรัพย์";
                    } else if (resultStatus.equals("case5")) {
                        legalStatusCode = "5";
                        legalStatusDesc = "ขายทอดตลาดได้แล้ว";
                    }

                    String dataStr = collID + "|" + legalID + "|" + legalStatusCode + "|" + legalStatusDesc + "|";

                    writer.write(dataStr);
                    writer.newLine();
                }
            }
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CMS", date);

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

    public String processLegalStatusCode(Map guaranteeInfo) {
        String statusCase = null;
        boolean case0 = false;
        boolean case1 = false;
        boolean case2 = false;
        boolean case3 = false;
        boolean case4 = false;
        boolean case5 = false;

        String document = guaranteeInfo.get("document").toString();
        String guarantee = guaranteeInfo.get("guaranteeID").toString();

        List<Map> documentHistoryList = cmsRepositoryCustom.findLegalStatusDocHistory(document);

        if (!documentHistoryList.isEmpty()) {
            if (AppUtil.isNotNull(documentHistoryList.get(0).get("proceedDocument"))) {

                String ProceedDocument = documentHistoryList.get(0).get("proceedDocument").toString();

                if (AppUtil.isNotNull(ProceedDocument)) {
                    if (ProceedDocument.equals("E3-7") || ProceedDocument.equals("F5-4") || ProceedDocument.equals("F9-4")) {
                        case3 = true;
                    }
                    if (ProceedDocument.equals("G22-1")) {
                        List<Map> assetSaleList = cmsRepositoryCustom.findLegalStatusAssetSale(guarantee);

                        if (!assetSaleList.isEmpty()) {
                            if (AppUtil.isNotNull(assetSaleList.get(0).get("resultSell"))) {
                                if (assetSaleList.get(0).get("resultSell").equals("1")) {
                                    case5 = true;
                                }
                            }
                        }
                    }
                }
            }

            if (AppUtil.isNotNull(documentHistoryList.get(0).get("docStatus"))) {
                String docStatus = documentHistoryList.get(0).get("docStatus").toString();
                if (docStatus.equals("A1") | docStatus.equals("A2")) {
                    case0 = true;
                }
                if (docStatus.equals("B1")) {
                    case1 = true;
                }
                if (docStatus.equals("E3")) {
                    case2 = true;
                }
            }
        }

        if ((AppUtil.isNotNull(guaranteeInfo.get("seizedCollateral")) && guaranteeInfo.get("seizedCollateral").equals("Y"))
                || (AppUtil.isNotNull(guaranteeInfo.get("seizedCollateralLawyer")) && guaranteeInfo.get("seizedCollateralLawyer").equals("Y"))
                || (AppUtil.isNotNull(guaranteeInfo.get("seizedCollateralGroup")) && guaranteeInfo.get("seizedCollateralGroup").equals("Y"))) {
            case4 = true;
        }

        if (case5) {
            statusCase = "case5";
        } else if (case4) {
            statusCase = "case4";
        } else if (case3) {
            statusCase = "case3";
        } else if (case2) {
            statusCase = "case2";
        } else if (case1) {
            statusCase = "case1";
        } else if (case0) {
            statusCase = "case0";
        }
        return statusCase;
    }

    @Override
    @SneakyThrows
    public void TBL_MT_COURT(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.TBL_MT_COURT");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("TBL_MT_COURT_YYYYMMDD.txt");

        try {
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            List<ParameterDetail> debtorList = parameterDetailRepository.findByPCode("COURT");

            String fileName = "TBL_MT_COURT_" + date + ".txt";
            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

            String total = "0";
            int seq = 0;

            if (!debtorList.isEmpty()) {
                total = String.valueOf(debtorList.size());
                for (ParameterDetail detail : debtorList) {
                    seq++;

                    String description = AppUtil.checkEmpty(detail.getDescription());
                    String status = AppUtil.checkEmpty(detail.getStatus());
                    String flag = "0";

                    if (status != null && status.equals("Y")) {
                        flag = "1";
                    }

                    ///write data in file
                    writer.write(seq + "|" + description + "|" + flag + "\n");
                }
            }

            writer.write("Total " + total + "\n");
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
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

    @Override
    @SneakyThrows
    public void TBL_MT_LED(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.TBL_MT_LED");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("TBL_MT_LED_YYYYMMDD.txt");
        try {
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            List<ParameterDetail> debtorList = parameterDetailRepository.findByPCode("LAGEL");

            String fileName = "TBL_MT_LED_" + date + ".txt";
            writer = new BufferedWriter(new FileWriter(path + "/" + fileName));
            String total = "0";
            int seq = 0;

            if (!debtorList.isEmpty()) {
                total = String.valueOf(debtorList.size());
                for (ParameterDetail detail : debtorList) {
                    seq++;

                    String description = AppUtil.checkEmpty(detail.getDescription());
                    String status = AppUtil.checkEmpty(detail.getStatus());
                    String flag = "0";

                    if (status != null && status.equals("Y")) {
                        flag = "1";
                    }

                    ///write data in file
                    writer.write(seq + "|" + description + "|" + flag + "\n");
                }
            }

            writer.write("Total " + total + "\n");
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "CMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
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
}
