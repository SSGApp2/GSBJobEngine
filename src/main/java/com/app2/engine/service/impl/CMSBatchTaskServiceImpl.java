package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.DocumentProgressRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.FileUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CMSBatchTaskServiceImpl extends AbstractEngineService implements CMSBatchTaskService{

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    GuaranteeInfoRepository guaranteeInfoRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentHistoryRepository documentHistoryRepository;

    @Autowired
    AssetSaleRepository assetSaleRepository;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ConfiscateRepository confiscateRepository;

    @Autowired
    DocumentProgressRepositoryCustom documentProgressRepositoryCustom;

    @Override
    public ResponseEntity<String> createFileCSVLitigationCVA() {
        String url = "/jobs/createFileCSVLitigationCVA";
        return getResultByExchange(url);
    }

    @Override
    @SneakyThrows
    public void SEIZE_INFO(String date) {
        String fileName = "SEIZEINFO_"+ date +".txt";
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","03");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);
        String dataHeaderStr = "collID|isSeizeCancel|seizeDecidedCaseNo|seizeDate|seizeBy|seizeJudgeDate|seizeLawCourt|seizeComplaintant|seizeExecution|seizeCancelDate|isSeize|";

        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

        writer.write(dataHeaderStr);
        writer.newLine();

        List<GuaranteeInfo> guaranteeInfoList = guaranteeInfoRepository.findAllBySeizedCollateral("Y");

        for (int i=0 ; i<guaranteeInfoList.size() ; i++){
            GuaranteeInfo guaranteeInfo = guaranteeInfoList.get(i);
            String collID = null;
            String isSeizeCancel = "0";
            String seizeDecidedCaseNo = "";
            Date seizeDate = null;
            String seizeLawCourt = "";
            String seizeJudgeDate = "";
            String seizeExecution = "";

            collID = guaranteeInfo.getDocNumber();


            Long idDoc = guaranteeInfo.getDocument().getId();
            Document document  = documentRepository.findOneById(idDoc);



            if (AppUtil.isNotNull(document)){
                List<DocumentProgress> docRedCaseNumberList = new ArrayList<>();
                List<DocumentProgress> docBlackCaseNumberList = new ArrayList<>();
                List<DocumentProgress> docBlackAndRedCaseNumberList = new ArrayList<>();

//                List<DocumentProgress> documentProgressList = documentProgressRepository.findByDocument(document);
                List<DocumentProgress> documentProgressList = documentProgressRepositoryCustom.findByDocumentId(document.getId());


                for (int j=0 ; j<documentProgressList.size() ; j++){
                    DocumentProgress documentProgress = documentProgressList.get(j);
                    if (AppUtil.isNotNull(documentProgress.getRedCaseNumber()) && AppUtil.isNull(documentProgress.getBlackCaseNumber())){
                        docRedCaseNumberList.add(documentProgress);
                    }else if (AppUtil.isNotNull(documentProgress.getBlackCaseNumber()) && AppUtil.isNull(documentProgress.getRedCaseNumber())){
                        docBlackCaseNumberList.add(documentProgress);
                    }else if (AppUtil.isNotNull(documentProgress.getRedCaseNumber()) && AppUtil.isNotNull(documentProgress.getBlackCaseNumber())){
                        docBlackAndRedCaseNumberList.add(documentProgress);
                    }
                }

                if (!documentProgressList.isEmpty()){
                    if (!docRedCaseNumberList.isEmpty()){
                        DocumentProgress documentProgress = latestDayByUpdateDate(docRedCaseNumberList);
                        seizeDecidedCaseNo = documentProgress.getRedCaseNumber();
                    }else if (!docBlackCaseNumberList.isEmpty()){
                        DocumentProgress documentProgress = latestDayByUpdateDate(docBlackCaseNumberList);
                        seizeDecidedCaseNo = documentProgress.getBlackCaseNumber();
                    }else if (!docBlackAndRedCaseNumberList.isEmpty()){
                        DocumentProgress documentProgress = latestDayByUpdateDate(docBlackAndRedCaseNumberList);
                        seizeDecidedCaseNo = documentProgress.getRedCaseNumber();
                    }
                }
            }

            List<Confiscate> confiscateList = guaranteeInfo.getConfiscates();

            if (confiscateList.size() == 1){
                seizeDate = confiscateList.get(0).getConfiscateDate();
                seizeLawCourt = confiscateList.get(0).getCourtAdjudicate();
            }else if (confiscateList.size() > 1){
                List<Confiscate> confiscateList1 = confiscateRepository.findAllByGuaranteeInfoOrderByConfiscateDate(guaranteeInfo);
                int size = confiscateList1.size()-1;
                seizeDate = confiscateList1.get(size).getConfiscateDate();
                seizeLawCourt = confiscateList1.get(size).getCourtAdjudicate();
            }

            if (AppUtil.isNotNull(document)){
//                seizeJudgeDate = guaranteeInfo.getDocument().toString();
                seizeExecution = guaranteeInfo.getDocument().getOfficeLegal();
            }

            String dataStr = collID+"|"+isSeizeCancel+"|"+seizeDecidedCaseNo+"|"+seizeDate+"||"+seizeLawCourt+"|"+seizeJudgeDate+"||"+seizeExecution+"|||";
            writer.write(dataStr);
            writer.newLine();
        }
        writer.close();

        //Copy file to FTP Server
        smbFileService.localFileToRemoteFile(fileName, "CMS", date);
    }

    public DocumentProgress latestDayByUpdateDate(List<DocumentProgress> documentProgressList){
        DocumentProgress docProgressLast = null;
        Long dateMax = 0L;
        for (int x=0 ; x<documentProgressList.size() ; x++){
            Date dateDocRedCase = documentProgressList.get(x).getUpdatedDate();
            Long dateLong = dateDocRedCase.getTime();
            if (dateLong > dateMax){
                docProgressLast = documentProgressList.get(x);
                dateMax = dateLong;
            }
        }
        return docProgressLast;
    }

    @Override
    @SneakyThrows
    public void LEGAL_STATUS(String date) {
        String fileName = "LEGAL_STATUS_"+ date +".txt";
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","03");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);
        String dataHeaderStr = "collID|legalID|legalStatusCode|legalStatusDesc|";
        String total = "0";

        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

        writer.write(dataHeaderStr);
        writer.newLine();

        List<GuaranteeInfo> guaranteeInfoList = guaranteeInfoRepository.findAll();

        for (int i=0 ; i<guaranteeInfoList.size() ; i++){
            GuaranteeInfo guaranteeInfo = guaranteeInfoList.get(i);
            String resultStatus = processLegalStatusCode(guaranteeInfo);
            String collID = guaranteeInfo.getDocNumber();
            String legalID = "null"; //ยังไม่มี
            String legalStatusCode = null;
            String legalStatusDesc = null;


            if (AppUtil.isNotNull(resultStatus)){
                if (resultStatus.equals("case0")){
                    legalStatusCode = "0";
                    legalStatusDesc = "ไม่มี Desc";
                }else if (resultStatus.equals("case1")){
                    legalStatusCode = "1";
                    legalStatusDesc = "ส่งดำเนินคดี";
                }else if (resultStatus.equals("case2")){
                    legalStatusCode = "2";
                    legalStatusDesc = "ยื่นฟ้องดำเนินคดี";
                }else if (resultStatus.equals("case3")){
                    legalStatusCode = "3";
                    legalStatusDesc = "พิพากษา";
                }else if (resultStatus.equals("case4")){
                    legalStatusCode = "4";
                    legalStatusDesc = "ยึดทรัพย์";
                }else if (resultStatus.equals("case5")){
                    legalStatusCode = "5";
                    legalStatusDesc = "ขายทอดตลาดได้แล้ว";
                }

                String dataStr = collID+"|"+legalID+"|"+legalStatusCode+"|"+legalStatusDesc+"|";

                writer.write(dataStr);
                writer.newLine();
            }

        }
        writer.close();

        //Copy file to FTP Server
        smbFileService.localFileToRemoteFile(fileName, "CMS", date);
    }

    public String processLegalStatusCode(GuaranteeInfo guaranteeInfo){
        String statusCase = null;
        boolean case0 = false;
        boolean case1 = false;
        boolean case2 = false;
        boolean case3 = false;
        boolean case4 = false;
        boolean case5 = false;

        List<Document> documentList = documentRepository.findByGuaranteeInfos(guaranteeInfo);

        if (!documentList.isEmpty()){
            Document document = documentList.get(0);

            List<DocumentHistory> documentHistoryList = documentHistoryRepository.findByDocumentOrderBySequenceDesc(document);

            if (!documentHistoryList.isEmpty()){
                if (AppUtil.isNotNull(documentHistoryList.get(0).getDocStatus())){

                    String ProceedDocument = documentHistoryList.get(0).getProceedDocument();

                    if (AppUtil.isNotNull(ProceedDocument)){
                        if (ProceedDocument.equals("E3-7") || ProceedDocument.equals("F5-4") || ProceedDocument.equals("F9-4")){
                            case3 = true;
                        }
                        if (ProceedDocument.equals("G22-1")){
                            List<AssetSale> assetSaleList = assetSaleRepository.findByGuaranteeInfoOrderBySaleTimeDesc(guaranteeInfo);

                            if (!assetSaleList.isEmpty()){
                                if (AppUtil.isNotNull(assetSaleList.get(0).getResultSell())){
                                    if (assetSaleList.get(0).getResultSell().equals("1")){
                                        case5 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (AppUtil.isNotNull(document.getDocStatus())){
                if (document.getDocStatus().equals("A1") | document.getDocStatus().equals("A2")){
                    case0 = true;
                }
                if (document.getDocStatus().equals("B1")){
                    case1 = true;
                }
                if (document.getDocStatus().equals("E3")){
                    case2 = true;
                }
            }

        }

        if ((AppUtil.isNotNull(guaranteeInfo.getSeizedCollateral()) && guaranteeInfo.getSeizedCollateral().equals("Y"))
                || (AppUtil.isNotNull(guaranteeInfo.getSeizedCollateralLawyer()) && guaranteeInfo.getSeizedCollateralLawyer().equals("Y"))
                || (AppUtil.isNotNull(guaranteeInfo.getSeizedCollateralGroup()) && guaranteeInfo.getSeizedCollateralGroup().equals("Y"))){
            case4 = true;
        }

        if (case5){
            statusCase = "case5";
        }else if (case4){
            statusCase = "case4";
        }else if (case3){
            statusCase = "case3";
        }else if (case2){
            statusCase = "case2";
        }else if (case1){
            statusCase = "case1";
        }else if (case0){
            statusCase = "case0";
        }
        return statusCase;
    }

    @Override
    public void TBL_MT_COURT(String date) {
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

        String fileName = "TBL_MT_COURT_" + date + ".txt";
        File file = new File(path + "/" + fileName);

        List<ParameterDetail> debtorList = parameterDetailRepository.findByPCode("COURT");

        FileWriter writer = null;
        String total = "0";
        int i = 0;

        try {
            //delete old file
            if (file.exists() && file.isFile()) {
                file.delete();
            }

            //create new file
            writer = new FileWriter(file, true);

            if (!debtorList.isEmpty()) {
                total = String.valueOf(debtorList.size());
                for (ParameterDetail detail : debtorList) {
                    i++;

                    String description = AppUtil.checkEmpty(detail.getDescription());
                    String status = AppUtil.checkEmpty(detail.getStatus());
                    String flag = "0";

                    if (status != null && status.equals("Y")) {
                        flag = "1";
                    }

                    ///write data in file
                    writer.write(i + "|" + description + "|" + flag + "\n");
                }
            }

            writer.write("Total " + total + "\n");

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(file.getName(), "CMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void TBL_MT_LED(String date) {
        ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "03");

        //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
        String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

        String fileName = "TBL_MT_LED_" + date + ".txt";
        File file = new File(path + "/" + fileName);

        List<ParameterDetail> debtorList = parameterDetailRepository.findByPCode("LAGEL");
        FileWriter writer = null;
        String total = "0";
        int i = 0;

        try {
            //delete old file
            if (file.exists() && file.isFile()) {
                file.delete();
            }

            //create new file
            writer = new FileWriter(file, true);

            if (!debtorList.isEmpty()) {
                total = String.valueOf(debtorList.size());
                for (ParameterDetail detail : debtorList) {
                    i++;

                    String description = AppUtil.checkEmpty(detail.getDescription());
                    String status = AppUtil.checkEmpty(detail.getStatus());
                    String flag = "0";

                    if (status != null && status.equals("Y")) {
                        flag = "1";
                    }

                    ///write data in file
                    writer.write(i + "|" + description + "|" + flag + "\n");
                }
            }

            writer.write("Total " + total + "\n");

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(file.getName(), "CMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
