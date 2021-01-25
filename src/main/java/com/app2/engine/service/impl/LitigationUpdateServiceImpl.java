package com.app2.engine.service.impl;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.repository.custom.DCMSRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LitigationUpdateServiceImpl extends AbstractEngineService implements LitigationUpdateService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    DCMSRepositoryCustom dcmsRepositoryCustom;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    private enum BKC_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, NOTICE_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, BANKRUPT_DT, GAZETTE_DT, DUE_DT, SETTLEMENT_DT, SETTLEMENT_SEQ, PRINCIPAL_AMT, INTEREST_AMT, COURT_DT, COURT_SEQ, COURT_PRINCIPAL_AMT, COURT_INTEREST_AMT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum BKO_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DT, PLAINTIFF, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, BANKRUPT_DT, GAZETTE_DT, DUE_DT, SETTLEMENT_DT, SETTLEMENT_SEQ, PRINCIPAL_AMT, INTEREST_AMT, COURT_DT, COURT_SEQ, COURT_PRINCIPAL_AMT, COURT_INTEREST_AMT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVA_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_DT, JUDGMENT_RESULT_DESC, JUDGMENT_AMOUNT, APPEAL_RESULT_DESC, DEKA_RESULT_DESC, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVC_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, NOTICE_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, JUDGMENT_AMOUNT, APPEAL_DECIDED_NO, APPEAL_DECIDED_YEAR, APPEAL_DT, APPEAL_RESULT_DESC, APPEAL_AMOUNT, DEKA_DECIDED_NO, DEKA_DECIDED_YEAR, DEKA_DT, DEKA_RESULT_DESC, DEKA_AMOUNT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVO_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, PLAINTIFF, PT_JUDGMENT_DECIDED_NO, PT_JUDGMENT_DECIDED_YEAR, PT_JUDGMENT_DT, PT_SEIZE_DT, ASSIGN_LAWYER_DT, OFFICER_SENDDOC_DT, PREFERED_DEBT_DT, PREFERED_DEBT_AMOUNT, PREFERED_COURT_DT, PREFERED_COURT_AMOUNT, SM_JUDGMENT_UNDECIDED_NO, SM_JUDGMENT_UNDECIDED_YEAR, SM_JUDGMENT_SUE_DT, SM_JUDGMENT_DECIDED_NO, SM_JUDGMENT_DECIDED_YEAR, SM_JUDGMENT_DT, SM_APPEAL_AMOUNT, APPEAL_DT, APPEAL_AMOUNT, APPEAL_COURT_DT, APPEAL_COURT_AMOUNT, AP_JUDGMENT_UNDECIDED_NO, AP_JUDGMENT_UNDECIDED_YEAR, AP_JUDGMENT_SUE_DT, AP_JUDGMENT_DECIDED_NO, AP_JUDGMENT_DECIDED_YEAR, AP_JUDGMENT_DT, AP_APPEAL_AMOUNT, AUCTION_TYPE, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private char delimiterPipe = '|';

    @SneakyThrows
    public String convertDateToFile(String pattern, String dateTime) {
        String newDate = "";
        if (AppUtil.isNotNull(dateTime)) {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateUtil.getSystemLocale()).parse(dateTime);
            SimpleDateFormat newformat = new SimpleDateFormat(pattern, Locale.US);
            newDate = newformat.format(date);
        }
        return newDate;
    }

    public String convertDoubleToString(Double value) {
        String newValue = "";
        if (AppUtil.isNotNull(value)) {
            DecimalFormat df = new DecimalFormat("#.00");
            newValue = df.format(value);
        }
        return newValue;
    }

    @SneakyThrows
    @Override
    public void litigationUpdateBKC(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateBKC");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LitigationUpdate_BKC_YYYYMMDD.csv");
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_BKC_" + date + ".csv";

            writer = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));

            writer.write('\ufeff'); ///รองรับภาษาไทย
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(BKC_HEADER.class));

            List<Map<String, Object>> listMap = dataForBKC(date);
            String total = String.format("%010d", listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(BKC_HEADER.SEQ.toString())
                        , objectMap.get(BKC_HEADER.LEGAL_ID.toString())
                        , objectMap.get(BKC_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(BKC_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(BKC_HEADER.ACN.toString())
                        , objectMap.get(BKC_HEADER.COLL_ID.toString())
                        , objectMap.get(BKC_HEADER.COLL_TYPE.toString())
                        , objectMap.get(BKC_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(BKC_HEADER.NOTICE_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(BKC_HEADER.BANKRUPT_DT.toString())
                        , objectMap.get(BKC_HEADER.GAZETTE_DT.toString())
                        , objectMap.get(BKC_HEADER.DUE_DT.toString())
                        , objectMap.get(BKC_HEADER.SETTLEMENT_DT.toString())
                        , objectMap.get(BKC_HEADER.SETTLEMENT_SEQ.toString())
                        , objectMap.get(BKC_HEADER.PRINCIPAL_AMT.toString())
                        , objectMap.get(BKC_HEADER.INTEREST_AMT.toString())
                        , objectMap.get(BKC_HEADER.COURT_DT.toString())
                        , objectMap.get(BKC_HEADER.COURT_SEQ.toString())
                        , objectMap.get(BKC_HEADER.COURT_PRINCIPAL_AMT.toString())
                        , objectMap.get(BKC_HEADER.COURT_INTEREST_AMT.toString())
                        , objectMap.get(BKC_HEADER.SEIZE_DT.toString())
                        , objectMap.get(BKC_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(BKC_HEADER.APPROVED_DT.toString())
                        , objectMap.get(BKC_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(BKC_HEADER.AUCTION_DT.toString())
                        , objectMap.get(BKC_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(BKC_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : " + total);
            csvPrinter.flush();
            csvPrinter.close();

            writer.close();
            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());

            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
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

    @Transactional
    public List<Map<String, Object>> dataForBKC(String date) {
        String newDate = DateUtil.convertStringDateToString(date);

        List<Map<String, Object>> listMap = new ArrayList<>();

        // --- Get data For file here
        List<Map> maps = dcmsRepositoryCustom.litigationUpdateBKC(newDate);
        List<Map> listData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (!maps.isEmpty()) {
            for (Map bkc : maps) {
                List<Map> mapDup = listData
                        .stream()
                        .filter(value -> value.get("id").toString().equals(bkc.get("id").toString()))
                        .collect(Collectors.toList());
                if (mapDup.isEmpty()) {
                    if (bkc.get("doc_number") != null && bkc.get("action_time") != null
                            && bkc.get("notic_doc_send_date") != null && bkc.get("black_case_number") != null
                            && bkc.get("law_suit_send_date") != null && bkc.get("red_case_number") != null
                            && bkc.get("adjudication") != null && bkc.get("adj_date") != null
                            && bkc.get("gazette_date") != null && bkc.get("account_payment_date") != null
                            && bkc.get("debt_amount") != null && bkc.get("amount") != null
                            && bkc.get("confiscate_date") != null && bkc.get("cost_est_legal_ex_office") != null
                            && bkc.get("cost_est_legal_bank_date") != null && bkc.get("amount_buy") != null
                            && bkc.get("doc_status") != null) {
                        listData.add(bkc);
                    }
                }
            }

            for (int i = 0; i < listData.size(); i++) {
                Map<String, Object> BKCMap = new HashMap();
                Map dataMap = mapper.convertValue(listData.get(i), Map.class);

                String[] blackCaseNumber = dataMap.get("black_case_number").toString().split("/");
                String blackCaseNumberOne = blackCaseNumber[0];
                String blackCaseNumberTwo = blackCaseNumber[1];

                String[] redCaseNumber = dataMap.get("red_case_number").toString().split("/");
                String redCaseNumberOne = redCaseNumber[0];
                String redCaseNumberTwo = redCaseNumber[1];

                Parameter parameter = parameterRepository.findByCode("ADJUDICATION");
                List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);
                String adjudication = "";
                for (ParameterDetail detail : parameterDetails) {
                    if (dataMap.get("adjudication").toString().equals(detail.getCode())) {
                        adjudication = detail.getName();
                    }
                }

                BKCMap.put(BKC_HEADER.SEQ.toString(), i + 1);
                BKCMap.put(BKC_HEADER.LEGAL_ID.toString(), dataMap.get("doc_number").toString());
                BKCMap.put(BKC_HEADER.WF_TYPE_ID.toString(), "");
                BKCMap.put(BKC_HEADER.WF_TYPE_DESC.toString(), "");
                BKCMap.put(BKC_HEADER.ACN.toString(), "");
                BKCMap.put(BKC_HEADER.COLL_ID.toString(), "");
                BKCMap.put(BKC_HEADER.COLL_TYPE.toString(), "");
                BKCMap.put(BKC_HEADER.ASSIGN_LAWYER_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("action_time").toString()));
                BKCMap.put(BKC_HEADER.NOTICE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("notic_doc_send_date").toString()));
                BKCMap.put(BKC_HEADER.JUDGMENT_UNDECIDED_NO.toString(), blackCaseNumberOne);
                BKCMap.put(BKC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(), blackCaseNumberTwo);
                BKCMap.put(BKC_HEADER.JUDGMENT_SUE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("law_suit_send_date").toString()));
                BKCMap.put(BKC_HEADER.JUDGMENT_DT.toString(), "");
                BKCMap.put(BKC_HEADER.JUDGMENT_DECIDED_NO.toString(), redCaseNumberOne);
                BKCMap.put(BKC_HEADER.JUDGMENT_DECIDED_YEAR.toString(), redCaseNumberTwo);
                BKCMap.put(BKC_HEADER.JUDGMENT_RESULT_DESC.toString(), adjudication);
                BKCMap.put(BKC_HEADER.BANKRUPT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("adj_date").toString()));
                BKCMap.put(BKC_HEADER.GAZETTE_DT.toString(), dataMap.get("gazette_date").toString());
                BKCMap.put(BKC_HEADER.DUE_DT.toString(), "");
                BKCMap.put(BKC_HEADER.SETTLEMENT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("account_payment_date").toString()));
                BKCMap.put(BKC_HEADER.SETTLEMENT_SEQ.toString(), "");
                BKCMap.put(BKC_HEADER.PRINCIPAL_AMT.toString(), String.format("%.2f", dataMap.get("debt_amount")));
                BKCMap.put(BKC_HEADER.INTEREST_AMT.toString(), String.format("%.2f", dataMap.get("amount")));
                BKCMap.put(BKC_HEADER.COURT_DT.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_SEQ.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_PRINCIPAL_AMT.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_INTEREST_AMT.toString(), "");
                BKCMap.put(BKC_HEADER.SEIZE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("confiscate_date").toString()));
                BKCMap.put(BKC_HEADER.LED_APPRAISAL.toString(), dataMap.get("cost_est_legal_ex_office").toString());
                BKCMap.put(BKC_HEADER.APPROVED_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("cost_est_legal_bank_date").toString()));
                BKCMap.put(BKC_HEADER.APPRAISAL_VAL.toString(), "");
                BKCMap.put(BKC_HEADER.AUCTION_DT.toString(), "");
                BKCMap.put(BKC_HEADER.AUCTION_AMT.toString(), String.format("%.2f", dataMap.get("amount_buy")));
                BKCMap.put(BKC_HEADER.LITIGTION_STATUS.toString(), dataMap.get("doc_status").toString());

                listMap.add(BKCMap);
            }
        }
        return listMap;
    }

    @Override
    @SneakyThrows
    public void litigationUpdateBKO(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateBKO");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LitigationUpdate_BKO_YYYYMMDD.csv");
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_BKO_" + date + ".csv";

            writer = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));

            writer.write('\ufeff');/// รองรับภาษาไทย
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(BKO_HEADER.class));

            List<Map<String, Object>> listMap = dataForBKO(date);
            String total = String.format("%010d", listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(BKO_HEADER.SEQ.toString())
                        , objectMap.get(BKO_HEADER.LEGAL_ID.toString())
                        , objectMap.get(BKO_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(BKO_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(BKO_HEADER.ACN.toString())
                        , objectMap.get(BKO_HEADER.COLL_ID.toString())
                        , objectMap.get(BKO_HEADER.COLL_TYPE.toString())
                        , objectMap.get(BKO_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(BKO_HEADER.PLAINTIFF.toString())/////don't have in excel
                        , objectMap.get(BKO_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(BKO_HEADER.BANKRUPT_DT.toString())
                        , objectMap.get(BKO_HEADER.GAZETTE_DT.toString())
                        , objectMap.get(BKO_HEADER.DUE_DT.toString())
                        , objectMap.get(BKO_HEADER.SETTLEMENT_DT.toString())
                        , objectMap.get(BKO_HEADER.SETTLEMENT_SEQ.toString())
                        , objectMap.get(BKO_HEADER.PRINCIPAL_AMT.toString())
                        , objectMap.get(BKO_HEADER.INTEREST_AMT.toString())
                        , objectMap.get(BKO_HEADER.COURT_DT.toString())
                        , objectMap.get(BKO_HEADER.COURT_SEQ.toString())
                        , objectMap.get(BKO_HEADER.COURT_PRINCIPAL_AMT.toString())
                        , objectMap.get(BKO_HEADER.COURT_INTEREST_AMT.toString())
                        , objectMap.get(BKO_HEADER.SEIZE_DT.toString())
                        , objectMap.get(BKO_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(BKO_HEADER.APPROVED_DT.toString())
                        , objectMap.get(BKO_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(BKO_HEADER.AUCTION_DT.toString())
                        , objectMap.get(BKO_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(BKO_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : " + total);
            csvPrinter.flush();
            csvPrinter.close();

            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
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
    public List<Map<String, Object>> dataForBKO(String date) {
        String newDate = DateUtil.convertStringDateToString(date);

        List<Map<String, Object>> listMap = new ArrayList<>();

        // --- Get data For file here
        List<Map> maps = dcmsRepositoryCustom.litigationUpdateBKO(newDate);
        List<Map> listData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (!maps.isEmpty()) {
            for (Map bko : maps) {
                List<Map> mapDup = listData
                        .stream()
                        .filter(value -> value.get("id").toString().equals(bko.get("id").toString()))
                        .collect(Collectors.toList());
                if (mapDup.isEmpty()) {
                    if (bko.get("doc_number") != null && bko.get("action_time") != null
                            && bko.get("black_case_number") != null && bko.get("red_case_number") != null
                            && bko.get("date_adjudicate_out") != null && bko.get("gazette_date") != null
                            && bko.get("account_payment_date") != null && bko.get("debt_amount") != null
                            && bko.get("amount") != null) {
                        listData.add(bko);
                    }
                }
            }

            for (int i = 0; i < listData.size(); i++) {
                Map<String, Object> BKOMap = new HashMap();
                Map dataMap = mapper.convertValue(listData.get(i), Map.class);

                String[] blackCaseNumber = dataMap.get("black_case_number").toString().split("/");
                String blackCaseNumberOne = blackCaseNumber[0];
                String blackCaseNumberTwo = blackCaseNumber[1];

                String[] redCaseNumber = dataMap.get("red_case_number").toString().split("/");
                String redCaseNumberOne = redCaseNumber[0];
                String redCaseNumberTwo = redCaseNumber[1];

                BKOMap.put(BKO_HEADER.SEQ.toString(), i + 1);
                BKOMap.put(BKO_HEADER.LEGAL_ID.toString(), dataMap.get("doc_number").toString());
                BKOMap.put(BKO_HEADER.WF_TYPE_ID.toString(), "0");
                BKOMap.put(BKO_HEADER.WF_TYPE_DESC.toString(), "0");
                BKOMap.put(BKO_HEADER.ACN.toString(), "0");
                BKOMap.put(BKO_HEADER.COLL_ID.toString(), "0");
                BKOMap.put(BKO_HEADER.COLL_TYPE.toString(), "0");
                BKOMap.put(BKO_HEADER.ASSIGN_LAWYER_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("action_time").toString()));
                BKOMap.put(BKO_HEADER.PLAINTIFF.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_UNDECIDED_NO.toString(), blackCaseNumberOne);
                BKOMap.put(BKO_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(), blackCaseNumberTwo);
                BKOMap.put(BKO_HEADER.JUDGMENT_SUE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_DECIDED_NO.toString(), redCaseNumberOne);
                BKOMap.put(BKO_HEADER.JUDGMENT_DECIDED_YEAR.toString(), redCaseNumberTwo);
                BKOMap.put(BKO_HEADER.JUDGMENT_RESULT_DESC.toString(), "0");
                BKOMap.put(BKO_HEADER.BANKRUPT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("date_adjudicate_out").toString()));
                BKOMap.put(BKO_HEADER.GAZETTE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("gazette_date").toString()));
                BKOMap.put(BKO_HEADER.DUE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.SETTLEMENT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("account_payment_date").toString()));
                BKOMap.put(BKO_HEADER.SETTLEMENT_SEQ.toString(), "0");
                BKOMap.put(BKO_HEADER.PRINCIPAL_AMT.toString(), String.format("%.2f", dataMap.get("debt_amount")));
                BKOMap.put(BKO_HEADER.INTEREST_AMT.toString(), String.format("%.2f", dataMap.get("amount")));
                BKOMap.put(BKO_HEADER.COURT_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_SEQ.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_PRINCIPAL_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_INTEREST_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.SEIZE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.LED_APPRAISAL.toString(), "0");
                BKOMap.put(BKO_HEADER.APPROVED_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.APPRAISAL_VAL.toString(), "0");
                BKOMap.put(BKO_HEADER.AUCTION_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.AUCTION_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.LITIGTION_STATUS.toString(), "0");

                listMap.add(BKOMap);
            }
        }
        return listMap;
    }

    @Override
    @SneakyThrows
    public void litigationUpdateCVA(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateCVA");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LitigationUpdate_CVA_YYYYMMDD.csv");

        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_CVA_" + date + ".csv";

            writer = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));

            writer.write('\ufeff');/// รองรับภาษาไทย
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVA_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVA();
            String total = String.format("%010d", listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVA_HEADER.SEQ.toString())
                        , objectMap.get(CVA_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVA_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVA_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVA_HEADER.ACN.toString())
                        , objectMap.get(CVA_HEADER.COLL_ID.toString())
                        , objectMap.get(CVA_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVA_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_AMOUNT.toString())
                        , objectMap.get(CVA_HEADER.APPEAL_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.DEKA_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVA_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVA_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVA_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVA_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVA_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVA_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : " + total);
            csvPrinter.flush();
            csvPrinter.close();

            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
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

    @Transactional
    public List<Map<String, Object>> dataForCVA() {
        List<Map<String, Object>> listMap = new ArrayList<>();
        // --- Get data For file here
        return listMap;
    }

    @SneakyThrows
    @Override
    public void litigationUpdateCVC(String date) {

        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.LitigationUpdateCVC");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LitigationUpdate_CVC_YYYYMMDD.csv");

        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_CVC_" + date + ".csv";

            writer = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));

            writer.write('\ufeff');/// รองรับภาษาไทย
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVC_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVC(date);
            String total = String.format("%010d", listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVC_HEADER.SEQ.toString())
                        , objectMap.get(CVC_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVC_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVC_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVC_HEADER.ACN.toString())
                        , objectMap.get(CVC_HEADER.COLL_ID.toString())
                        , objectMap.get(CVC_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVC_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVC_HEADER.NOTICE_DT.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DT.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DT.toString())
                        , objectMap.get(CVC_HEADER.DEKA_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.DEKA_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVC_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVC_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVC_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVC_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVC_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVC_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : " + total);
            csvPrinter.flush();
            csvPrinter.close();

            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
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

    @Transactional
    public List<Map<String, Object>> dataForCVC(String date) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        String fileDate = DateUtil.convertStringDateToString(date);
        ObjectMapper oMapper = new ObjectMapper();

        List resultCVCtList = findLitigationUpdateCVC(fileDate);

        for (int i = 0; i < resultCVCtList.size(); i++) {
            Map<String, Object> CVCMap = new HashMap();
            Map dataMap = oMapper.convertValue(resultCVCtList.get(i), Map.class);
            CVCMap.put(CVC_HEADER.SEQ.toString(), i + 1);
            CVCMap.put(CVC_HEADER.LEGAL_ID.toString(), dataMap.get("LEGAL_ID"));
            CVCMap.put(CVC_HEADER.WF_TYPE_ID.toString(), "");
            CVCMap.put(CVC_HEADER.WF_TYPE_DESC.toString(), "");
            CVCMap.put(CVC_HEADER.ACN.toString(), "");
            CVCMap.put(CVC_HEADER.COLL_ID.toString(), "");
            CVCMap.put(CVC_HEADER.COLL_TYPE.toString(), "");
            CVCMap.put(CVC_HEADER.ASSIGN_LAWYER_DT.toString(), dataMap.get("ASSIGN_LAWYER_DT"));
            CVCMap.put(CVC_HEADER.NOTICE_DT.toString(), "");
            CVCMap.put(CVC_HEADER.JUDGMENT_UNDECIDED_NO.toString(), dataMap.get("JUDGMENT_UNDECIDED_NO"));
            CVCMap.put(CVC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(), dataMap.get("JUDGMENT_UNDECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.JUDGMENT_SUE_DT.toString(), dataMap.get("JUDGMENT_SUE_DT"));
            CVCMap.put(CVC_HEADER.JUDGMENT_DECIDED_NO.toString(), dataMap.get("JUDGMENT_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.JUDGMENT_DECIDED_YEAR.toString(), dataMap.get("JUDGMENT_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.JUDGMENT_RESULT_DESC.toString(), dataMap.get("JUDGMENT_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.JUDGMENT_AMOUNT.toString(), dataMap.get("JUDGMENT_AMOUNT"));
            CVCMap.put(CVC_HEADER.APPEAL_DECIDED_NO.toString(), dataMap.get("APPEAL_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.APPEAL_DECIDED_YEAR.toString(), dataMap.get("APPEAL_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.APPEAL_DT.toString(), dataMap.get("APPEAL_DT"));
            CVCMap.put(CVC_HEADER.APPEAL_RESULT_DESC.toString(), dataMap.get("APPEAL_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.APPEAL_AMOUNT.toString(), dataMap.get("APPEAL_AMOUNT"));
            CVCMap.put(CVC_HEADER.DEKA_DECIDED_NO.toString(), dataMap.get("DEKA_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.DEKA_DECIDED_YEAR.toString(), dataMap.get("DEKA_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.DEKA_DT.toString(), dataMap.get("DEKA_DT"));
            CVCMap.put(CVC_HEADER.DEKA_RESULT_DESC.toString(), dataMap.get("DEKA_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.DEKA_AMOUNT.toString(), dataMap.get("DEKA_AMOUNT"));
            CVCMap.put(CVC_HEADER.SEIZE_DT.toString(), "");
            CVCMap.put(CVC_HEADER.LED_APPRAISAL.toString(), "");
            CVCMap.put(CVC_HEADER.APPROVED_DT.toString(), "");
            CVCMap.put(CVC_HEADER.APPRAISAL_VAL.toString(), "");
            CVCMap.put(CVC_HEADER.AUCTION_DT.toString(), "");
            CVCMap.put(CVC_HEADER.AUCTION_AMT.toString(), "");
            CVCMap.put(CVC_HEADER.LITIGTION_STATUS.toString(), "");
            listMap.add(CVCMap);
        }
        return listMap;
    }

    @Transactional
    public List findLitigationUpdateCVC(String curDate) {
        List completeResult = new ArrayList();

        List<Map> resultDocCurDateList = dcmsRepositoryCustom.litigationUpdateCVC(curDate);

        for (Map map : resultDocCurDateList) {
            Map<String, Object> resultCVCMap = new HashMap();
            String actionTimeLAW = null;
            String blackCaseNumNo = null;
            String blackCaseNumYear = null;
            String lawSuitSendDate = null;
            String redCaseNumNo1 = null;
            String redCaseNumYear1 = null;
            String adjDate1 = null;
            String adjudication1 = null;
            Double judgmentAmount1 = null;
            String redCaseNumNo2 = null;
            String redCaseNumYear2 = null;
            String adjDate2 = null;
            String adjudication2 = null;
            Double judgmentAmount2 = null;
            String redCaseNumNo3 = null;
            String redCaseNumYear3 = null;
            String adjDate3 = null;
            String adjudication3 = null;
            Double judgmentAmount3 = null;

            String docType = map.get("docType").toString();
            Double principalBalance = (Double) map.get("principalBalance");
            Double interest = (Double) map.get("interest");

            if (docType.equals("1")) {

                if (AppUtil.isNotNull(map.get("actionTime"))) {
                    actionTimeLAW = convertDateToFile("dd/MM/yyyy", map.get("actionTime").toString());
                }

                String redCaseNumberNo = "";
                String redCaseNumberYear = "";
                if (AppUtil.isNotNull(map.get("redCaseNumber"))) {
                    String redCaseNumber = map.get("redCaseNumber").toString();
                    String[] redCaseNumAr = redCaseNumber.split("/");
                    redCaseNumberNo = redCaseNumAr[0];
                    redCaseNumberYear = redCaseNumAr[1];
                }

                String adjDate = "";
                if (AppUtil.isNotNull(map.get("adjDate"))) {
                    adjDate = convertDateToFile("dd/MM/yyyy", map.get("adjDate").toString());
                }

                String adjudication = "";
                if (AppUtil.isNotNull(map.get("adjudication"))) {
                    adjudication = map.get("adjudication").toString();
                }

                if (AppUtil.isNotNull(map.get("blackCaseNumber")) && !map.get("blackCaseNumber").equals("/")) {
                    String blackCaseNumber = map.get("blackCaseNumber").toString();
                    String[] blackCaseNumAr = blackCaseNumber.split("/");
                    blackCaseNumNo = blackCaseNumAr[0];
                    blackCaseNumYear = blackCaseNumAr[1];
                }

                if (AppUtil.isNotNull(map.get("lawSuitSendDate"))) {
                    lawSuitSendDate = convertDateToFile("dd/MM/yyyy", map.get("lawSuitSendDate").toString());
                }

                if (AppUtil.isNotNull(map.get("court"))) {
                    String court = map.get("court").toString();

                    //ศาลชั้นต้น
                    if (court.equals("1")) {
                        redCaseNumNo1 = redCaseNumberNo;
                        redCaseNumYear1 = redCaseNumberYear;
                        adjDate1 = adjDate;
                        adjudication1 = adjudication;
                        judgmentAmount1 = principalBalance + interest;
                    }

                    //ศาลอุทธรณ์
                    if (court.equals("2")) {
                        redCaseNumNo2 = redCaseNumberNo;
                        redCaseNumYear2 = redCaseNumberYear;
                        adjDate2 = adjDate;
                        adjudication2 = adjudication;
                        judgmentAmount2 = principalBalance + interest;
                    }

                    //ศาลฎีกา
                    if (court.equals("3")) {
                        redCaseNumNo3 = redCaseNumberNo;
                        redCaseNumYear3 = redCaseNumberYear;
                        adjDate3 = adjDate;
                        adjudication3 = adjudication;
                        judgmentAmount3 = principalBalance + interest;
                    }
                }

                resultCVCMap.put("LEGAL_ID", map.get("docNumber"));
                resultCVCMap.put("ASSIGN_LAWYER_DT", actionTimeLAW);
                resultCVCMap.put("JUDGMENT_UNDECIDED_NO", blackCaseNumNo);
                resultCVCMap.put("JUDGMENT_UNDECIDED_YEAR", blackCaseNumYear);
                resultCVCMap.put("JUDGMENT_SUE_DT", lawSuitSendDate);

                resultCVCMap.put("JUDGMENT_DECIDED_NO", redCaseNumNo1);
                resultCVCMap.put("JUDGMENT_DECIDED_YEAR", redCaseNumYear1);
                resultCVCMap.put("JUDGMENT_DT", adjDate1);
                resultCVCMap.put("JUDGMENT_RESULT_DESC", adjudication1);
                resultCVCMap.put("JUDGMENT_AMOUNT", convertDoubleToString(judgmentAmount1));

                resultCVCMap.put("APPEAL_DECIDED_NO", redCaseNumNo2);
                resultCVCMap.put("APPEAL_DECIDED_YEAR", redCaseNumYear2);
                resultCVCMap.put("APPEAL_DT", adjDate2);
                resultCVCMap.put("APPEAL_RESULT_DESC", adjudication2);
                resultCVCMap.put("APPEAL_AMOUNT", convertDoubleToString(judgmentAmount2));

                resultCVCMap.put("DEKA_DECIDED_NO", redCaseNumNo3);
                resultCVCMap.put("DEKA_DECIDED_YEAR", redCaseNumYear3);
                resultCVCMap.put("DEKA_DT", adjDate3);
                resultCVCMap.put("DEKA_RESULT_DESC", adjudication3);
                resultCVCMap.put("DEKA_AMOUNT", convertDoubleToString(judgmentAmount3));
                completeResult.add(resultCVCMap);
            }
        }

        return completeResult;
    }

    @Override
    @SneakyThrows
    public void litigationUpdateCVO(String date) {
        BufferedWriter writer = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Upload.batchLitigationUpdateCVO");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LitigationUpdate_CVO_YYYYMMDD.csv");
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);
            String fileName = "/LitigationUpdate_CVO_" + date + ".csv";

            writer = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));


            writer.write('\ufeff');/// รองรับภาษาไทย
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVO_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVO(date);
            String total = String.format("%010d", listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVO_HEADER.SEQ.toString())
                        , objectMap.get(CVO_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVO_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVO_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVO_HEADER.ACN.toString())
                        , objectMap.get(CVO_HEADER.COLL_ID.toString())
                        , objectMap.get(CVO_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVO_HEADER.PLAINTIFF.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.PT_SEIZE_DT.toString())
                        , objectMap.get(CVO_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVO_HEADER.OFFICER_SENDDOC_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_DEBT_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_DEBT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_COURT_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_COURT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.SM_APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_DT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_COURT_DT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_COURT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.AP_APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_TYPE.toString())
                        , objectMap.get(CVO_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVO_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVO_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVO_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVO_HEADER.LITIGTION_STATUS.toString())
                );
            }
            csvPrinter.printRecord("TOTAL : " + total);
            csvPrinter.flush();
            csvPrinter.close();

            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());

            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
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

    @Transactional
    public List<Map<String, Object>> dataForCVO(String date) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        String fileDate = DateUtil.convertStringDateToString(date);
        ObjectMapper oMapper = new ObjectMapper();

        List resultCVOtList = findLitigationUpdateCVO(fileDate);

        for (int i = 0; i < resultCVOtList.size(); i++) {
            Map<String, Object> CVOMap = new HashMap();
            Map dataMap = oMapper.convertValue(resultCVOtList.get(i), Map.class);
            CVOMap.put(CVO_HEADER.SEQ.toString(), i + 1);
            CVOMap.put(CVO_HEADER.LEGAL_ID.toString(), dataMap.get("LEGAL_ID"));
            CVOMap.put(CVO_HEADER.WF_TYPE_ID.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.WF_TYPE_DESC.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.ACN.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.COLL_ID.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.COLL_TYPE.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.PLAINTIFF.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DECIDED_NO.toString(), "");
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DECIDED_YEAR.toString(), "");
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.PT_SEIZE_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.ASSIGN_LAWYER_DT.toString(), dataMap.get("ASSIGN_LAWYER_DT"));
            CVOMap.put(CVO_HEADER.OFFICER_SENDDOC_DT.toString(), dataMap.get("OFFICER_SENDDOC_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_DEBT_DT.toString(), dataMap.get("PREFERED_DEBT_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_DEBT_AMOUNT.toString(), dataMap.get("PREFERED_DEBT_AMOUNT"));
            CVOMap.put(CVO_HEADER.PREFERED_COURT_DT.toString(), dataMap.get("PREFERED_COURT_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_COURT_AMOUNT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_UNDECIDED_NO.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_UNDECIDED_YEAR.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_SUE_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DECIDED_NO.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DECIDED_YEAR.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_APPEAL_AMOUNT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPEAL_DT.toString(), dataMap.get("APPEAL_DT"));
            CVOMap.put(CVO_HEADER.APPEAL_AMOUNT.toString(), dataMap.get("APPEAL_AMOUNT"));
            CVOMap.put(CVO_HEADER.APPEAL_COURT_DT.toString(), dataMap.get("APPEAL_COURT_DT"));
            CVOMap.put(CVO_HEADER.APPEAL_COURT_AMOUNT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_UNDECIDED_NO.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_UNDECIDED_YEAR.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_SUE_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DECIDED_NO.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DECIDED_YEAR.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_APPEAL_AMOUNT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_TYPE.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.SEIZE_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.LED_APPRAISAL.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPROVED_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPRAISAL_VAL.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_DT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_AMT.toString(), dataMap.get(""));
            CVOMap.put(CVO_HEADER.LITIGTION_STATUS.toString(), dataMap.get(""));
            listMap.add(CVOMap);
        }
        return listMap;
    }

    @Transactional
    public List findLitigationUpdateCVO(String curDate) {
        List completeResult = new ArrayList();

        List<Map> resultDocCurDateList = dcmsRepositoryCustom.litigationUpdateCVO(curDate);

        for (Map map : resultDocCurDateList) {
            Map<String, Object> resultCVOMap = new HashMap();
            Object actionTimeLAW = null;
            Object actionTimeMK2 = null;
            String prefered_preferentialRequestDate = null;
            String prefered_amount = null;
            String prefered_permissDate = null;
            String appeal_preventRequestDate = null;
            String appeal_amount = null;
            String appeal_permissDate = null;

            if (AppUtil.isNotNull(map.get("userRoleTo"))) {
                String userRoleTo = map.get("userRoleTo").toString();
                String actionTime = convertDateToFile("dd/MM/yyyy", map.get("actionTime").toString());

                if (userRoleTo.equals("LAW")) {
                    actionTimeLAW = actionTime;
                }
                if (userRoleTo.equals("MK2")) {
                    actionTimeMK2 = actionTime;
                }

                String Amount = "";
                if (AppUtil.isNotNull(map.get("totalAmount"))) {
                    Amount = map.get("totalAmount").toString();
                }

                String PermissDate = "";
                if (AppUtil.isNotNull(map.get("permissDate"))) {
                    PermissDate = map.get("permissDate").toString();
                }

                if (AppUtil.isNotNull(map.get("typeProcess")) && AppUtil.isNotNull(map.get("docType"))) {
                    String typeProcess = map.get("typeProcess").toString();
                    String docType = map.get("docType").toString();

                    //การยื่นขอชำระหนี้
                    if (typeProcess.equals("1") && docType.equals("3")) {
                        if (AppUtil.isNotNull(map.get("preferentialRequestDate"))) {
                            prefered_preferentialRequestDate = map.get("preferentialRequestDate").toString();
                        }

                        prefered_amount = Amount;

                        prefered_permissDate = PermissDate;

                    }
                    //การร้องกันส่วน
                    if (typeProcess.equals("2") && docType.equals("3")) {
                        if (AppUtil.isNotNull(map.get("preventRequestDate"))) {
                            appeal_preventRequestDate = map.get("preventRequestDate").toString();
                        }

                        appeal_amount = Amount;

                        appeal_permissDate = PermissDate;

                    }
                }
            }

            resultCVOMap.put("LEGAL_ID", map.get("docNumber"));
            resultCVOMap.put("ASSIGN_LAWYER_DT", actionTimeLAW);
            resultCVOMap.put("OFFICER_SENDDOC_DT", actionTimeMK2);
            resultCVOMap.put("PREFERED_DEBT_DT", convertDateToFile("dd/MM/yyyy", prefered_preferentialRequestDate));
            resultCVOMap.put("PREFERED_DEBT_AMOUNT", prefered_amount);
            resultCVOMap.put("PREFERED_COURT_DT", convertDateToFile("dd/MM/yyyy", prefered_permissDate));
            resultCVOMap.put("APPEAL_DT", convertDateToFile("dd/MM/yyyy", appeal_preventRequestDate));
            resultCVOMap.put("APPEAL_AMOUNT", appeal_amount);
            resultCVOMap.put("APPEAL_COURT_DT", convertDateToFile("dd/MM/yyyy", appeal_permissDate));
            completeResult.add(resultCVOMap);
        }

        return completeResult;
    }

}
