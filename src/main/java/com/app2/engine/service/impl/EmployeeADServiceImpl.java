package com.app2.engine.service.impl;

import com.app2.engine.entity.app.EmployeeInternal;
import com.app2.engine.repository.EmployeeInternalRepository;
import com.app2.engine.service.EmployeeADService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
public class EmployeeADServiceImpl implements EmployeeADService {

    @Autowired
    EmployeeInternalRepository empRepo;

    @Autowired
    SmbFileService smbFileService;


    enum ADEmployee {
        employeeID, givenName, sn, displayName, description, title, employeeNumber, employeeType, postOfficeBox, mail, department, l, st, postalCode, division, otherPager, otherhomephone, otherIpPhone, streetAddress, userAccountControl, sAMAccountName, otherTelephone
    }


    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    @Override
//    @Transactional
    public void InsertOrUpdateEmp() {
        LOGGER.debug("Start InsertOrUpdateEmp {}", DateUtil.getCurrentDate());
        try {
            String fileName = "AD_20200525.csv";
//            String pathName = "C:\\Users\\thongchai_s\\Documents\\SoftsquareDoc\\GSB\\InterfaceAD\\encode\\" + fileName;
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(ADEmployee.class).parse(streamReader);
            LOGGER.debug("Path File {}", pathName);

            for (CSVRecord record : records) {
                Long row = record.getRecordNumber();
                if (row > 1) {

                    String empId = record.get(ADEmployee.employeeID);
                    String nameDesc = record.get(ADEmployee.description);
                    String fName = null, lName = null;
                    String fullName = null;
                    String empType = record.get(ADEmployee.employeeType);
                    String username = record.get(ADEmployee.sAMAccountName);
                    if (AppUtil.isNotEmpty(nameDesc)) {
                        fName = nameDesc.split(" ")[0];
                        lName = nameDesc.split(" ")[1];
                        fullName = fName + " " + lName;
                    }
                    String email = record.get(ADEmployee.mail);
                    LOGGER.debug(empId);
                    LOGGER.debug(fName);
                    LOGGER.debug(lName);
                    LOGGER.debug(email);
                    LOGGER.debug(empType);
                    LOGGER.debug(username);
                    if (AppUtil.isNotEmpty(username)) {
                        EmployeeInternal empInternal = empRepo.findByUsername(username);
                        if (AppUtil.isNull(empInternal)) {
                            empInternal = new EmployeeInternal();
                            empInternal.setUsername(username);

                        }
                        empInternal.setEmail(email);
                        empInternal.setFullName(fullName);
                        empInternal.setFirstName(fName);
                        empInternal.setLastName(lName);
                        empInternal.setEmpType(empType);
                        empRepo.save(empInternal);
                    }
                    LOGGER.debug("===========================================================");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }
}
