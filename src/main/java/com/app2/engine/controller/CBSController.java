package com.app2.engine.controller;

import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.SmbFileService;
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
@RequestMapping("/api/jobs/cbs/")
public class CBSController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @GetMapping("all")
    public void all(@RequestParam(value = "date", required = false) String date) {

        cbsBatchTaskService.LS_COLLECTION_STATUS(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : LS_COLLECTION_STATUS is completed.");

//        cbsBatchTaskService.ZLE(date != null?date:DateUtil.codeCurrentDate());
//        LOGGER.debug("Batch : ZLE is completed.");
//
//        cbsBatchTaskService.LS_COLLECTION_STATUS(date != null?date:DateUtil.codeCurrentDate());
//        LOGGER.debug("Batch : LS_COLLECTION_STATUS is completed.");
    }

    @GetMapping("downloadAll")
    public void downloadAll(@RequestParam(value = "date", required = false) String date) {
        // ประเทศ
        cbsBatchTaskService.MASTER_DATA_COUNTRY(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_COUNTRY is completed.");

        // จังหวัด
        cbsBatchTaskService.MASTER_DATA_PROVINCE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PROVINCE is completed.");

        // อำเภอ
        cbsBatchTaskService.MASTER_DATA_DISTRICT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_DISTRICT is completed.");

        // ตำบล
        cbsBatchTaskService.MASTER_DATA_SUB_DISTRICT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_SUB_DISTRICT is completed.");

        // สาขา
        cbsBatchTaskService.MASTER_DATA_BRANCH(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_BRANCH is completed.");

        // CostCenter
        cbsBatchTaskService.MASTER_DATA_COST_CENTER(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_COST_CENTER is completed.");

        // วันทำการธนาคาร
        cbsBatchTaskService.MASTER_DATA_WORKING_DAYS(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_WORKING_DAYS is completed.");

        // วันหยุดธนาคาร
//        cbsBatchTaskService.MASTER_DATA_HOLIDAY(date != null?date:DateUtil.codeCurrentDate());
//        LOGGER.debug("Batch : MASTER_DATA_HOLIDAY is completed.");

        // OU
        cbsBatchTaskService.MASTER_DATA_OU(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_OU is completed.");

        // Market Code
        cbsBatchTaskService.MASTER_DATA_MARKET_CODE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_MARKET_CODE is completed.");

        // Product Group
        cbsBatchTaskService.MASTER_DATA_PRODUCT_GROUP(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_GROUP is completed.");

        // Product Subtype
        cbsBatchTaskService.MASTER_DATA_PRODUCT_SUBTYPE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_GROUP is completed.");

        // Product Type
        cbsBatchTaskService.MASTER_DATA_PRODUCT_TYPE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_TYPE is completed.");

        // คำนำหน้า (แบบใหม่)
        cbsBatchTaskService.MASTER_DATA_TITLE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_TITLE is completed.");
    }

    @GetMapping("lsCollectionStatus")
    public void lsCollectionStatus(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.LS_COLLECTION_STATUS(date != null ? date : DateUtil.codeCurrentDate());
    }

    @GetMapping("zle")
    public void zle() {
        ResponseEntity<String> response = cbsBatchTaskService.batchZLETask();
        String fileName = response.getBody();
//        smbFileService.localFileToRemoteFile(fileName,"CBS");
    }

    @GetMapping("lsAcn")
    public void lsAcnTask() {
        String fileName = "LS_ACN_" + DateUtil.codeCurrentDate() + ".txt";
//        smbFileService.remoteFileToLocalFile(fileName,"CBS");
        ResponseEntity<String> response = cbsBatchTaskService.lsAcn();
    }

    @GetMapping("stblcntry")
    public void stblcntryTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_COUNTRY(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_COUNTRY is completed.");
    }

    @GetMapping("province")
    public void masterDataProvinceTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_PROVINCE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PROVINCE is completed.");
    }

    @GetMapping("district")
    public void masterDataDistrictTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_DISTRICT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_DISTRICT is completed.");
    }

    @GetMapping("ditrictCd")
    public void masterDataDistrictCdTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_SUB_DISTRICT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_SUB_DISTRICT is completed.");
    }

    @GetMapping("utblBrcd")
    public void masterDataBranchTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_BRANCH(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_BRANCH is completed.");
    }

    @GetMapping("utblcCntr")
    public void masterDataCostCenterTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_COST_CENTER(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_COST_CENTER is completed.");
    }

    @GetMapping("utblNbd")
    public void masterDataWorkingDaysTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_WORKING_DAYS(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_WORKING_DAYS is completed.");
    }

    @GetMapping("utblNbd1")
    public void masterDataHolidayTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_HOLIDAY(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_HOLIDAY is completed.");
    }

    @GetMapping("zutblOuBrcd")
    public void masterDataOUTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_OU(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_OU is completed.");
    }

    @GetMapping("mtMarketCode")
    public void masterDataMarketCodeTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_MARKET_CODE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_MARKET_CODE is completed.");
    }

    @GetMapping("mtProductGroup")
    public void masterDataProductGroupTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_PRODUCT_GROUP(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_GROUP is completed.");
    }

    @GetMapping("mtProductSubType")
    public void masterDataProductSubtypeTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_PRODUCT_SUBTYPE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_GROUP is completed.");
    }

    @GetMapping("mtProductType")
    public void masterDataProductTypeTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_PRODUCT_TYPE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_PRODUCT_TYPE is completed.");
    }

    @GetMapping("zutblTitle")
    public void masterDataTitleTask(@RequestParam(value = "date", required = false) String date) {
        cbsBatchTaskService.MASTER_DATA_TITLE(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : MASTER_DATA_TITLE is completed.");
    }

}
