package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.AppUserRepositoryCustom;
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

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeADServiceImpl implements EmployeeADService {

    @Autowired
    EmployeeInternalRepository empRepo;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    OrgGroupRepository orgGroupRepository;

    @Autowired
    LineBusinessRepository lineBusinessRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    AppUserRepositoryCustom appUserRepositoryCustom;

    enum ADEmployee {
        employeeID, givenName, sn, displayName, description, title, employeeNumber, employeeType, postOfficeBox, mail, department, l, st, postalCode, division, otherPager, otherhomephone, otherIpPhone, streetAddress, userAccountControl, sAMAccountName, otherTelephone
    }


    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    @Override
//    @Transactional
    public void InsertOrUpdateEmp() {
        LOGGER.debug("Start InsertOrUpdateEmp {}", DateUtil.getCurrentDate());
        try {
//            String fileName = "AD_20200525-Edit.csv";
            String fileName = "AD_20200525.csv";
//            String pathName = "C:\\Users\\thongchai_s\\Documents\\SoftsquareDoc\\GSB\\InterfaceAD\\encode\\" + fileName;
            String pathName = smbFileService.remoteFileToLocalFile(fileName,"AD");
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(ADEmployee.class).parse(streamReader);
            LOGGER.debug("Path File {}", pathName);
            List<String> usernameActive = new ArrayList<>();
            for (CSVRecord record : records) {
                Long row = record.getRecordNumber();
                if (row > 1) {

                    String empId = record.get(ADEmployee.employeeID);
                    String nameDesc = record.get(ADEmployee.description);
                    String fName = null, lName = null, OU = null, orgGroupCode = null, lineBusinessCode = null, zoneCode = null, areaCode = null, branchCode = null, unitCode = null, subUnit = null;
                    String fullName = null, departmentForLead = null;
                    String empType = record.get(ADEmployee.employeeType);
                    String username = record.get(ADEmployee.sAMAccountName).toLowerCase();
                    String posName = record.get(ADEmployee.title);
                    ///////////////////////////////////////////
                    OrgGroup orgGroup = null;
                    LineBusiness lineBusiness = null;
                    Zone zone = null;
                    Branch branch = null;
                    Unit unit = null;
                    Position position = null;
                    ///////////////////////////////////////////

                    if (AppUtil.isNotEmpty(nameDesc)) {
                        fName = nameDesc.split(" ")[0];
                        lName = nameDesc.split(" ")[1];
                        fullName = fName + " " + lName;
                    }
                    String division = record.get(ADEmployee.division);
                    if (AppUtil.isNotEmpty(division)) {
                        List<String> array = new ArrayList<>();
                        String value = "";
                        for (int i = 1; i <= division.length(); i++) {
                            value += division.charAt(i - 1);
                            if (i % 8 == 0) {
                                array.add(value);
                                value = "";
                            }
                        }
                        LOGGER.debug("ARRAY 64 {}", array);
                        if (array.size() == 8) {
                            OU = array.get(0);
                            orgGroup = orgGroupRepository.findOneByCode(array.get(1));
                            lineBusiness = lineBusinessRepository.findOneByCode(array.get(2));
                            zoneCode = array.get(3);
                            zone = zoneRepository.findOneByCode(zoneCode);
                            areaCode = array.get(4);
                            branchCode = array.get(5);
                            if (branchCode.equals("00000000")) {
                                departmentForLead = zoneCode;
                            } else {
                                branch = branchRepository.findOneByCode(branchCode);
                                departmentForLead = branchCode;
                            }

                            unit = unitRepository.findOneByCode(array.get(6));
                            subUnit = array.get(7);
                        }
                    }
                    if (AppUtil.isNotEmpty(posName)) {
                        Position posQuery = positionRepository.findByName(posName.trim());
                        if (AppUtil.isNotNull(posQuery)) {
                            position = posQuery;
                        }
                    }

                    String email = record.get(ADEmployee.mail);
                    LOGGER.debug("empId :{}", empId);
                    LOGGER.debug("fName :{}", fName);
                    LOGGER.debug("lName :{}", lName);
                    LOGGER.debug("email :{}", email);
                    LOGGER.debug("empType :{}", empType);
                    LOGGER.debug("username :{}", username);
                    LOGGER.debug("division :{}", division);
                    LOGGER.debug("posName :{}", posName);


                    if (AppUtil.isNotEmpty(username)) {
                        usernameActive.add(username);
                        EmployeeInternal empInternal = empRepo.findByUsername(username);
                        if (AppUtil.isNull(empInternal)) {
                            empInternal = new EmployeeInternal();
                            empInternal.setUsername(username);
                        }
                        AppUser appUser = appUserRepository.findByUsernameInternal(username);
                        if (AppUtil.isNull(appUser)) { //add appuser when new employee
                            appUser = new AppUser();
                            appUser.setUsername(username);
                            appUser.setUserType("I"); //internal
                            appUser.setStatus("A"); //Active
                            appUserRepository.save(appUser);
                        }


                        empInternal.setPosition(position);
                        empInternal.setEmail(email);
                        empInternal.setFullName(fullName);
                        empInternal.setFirstName(fName);
                        empInternal.setLastName(lName);
                        empInternal.setEmpType(empType);

                        empInternal.setOrgGroup(orgGroup);
                        empInternal.setLineBusiness(lineBusiness);
                        empInternal.setZone(zone);
                        empInternal.setUnit(unit);
                        empInternal.setBranch(branch);
                        if(String.valueOf(empInternal.getTempBranch()).equals("Y")){
                            //Y:สาขาชั่วคราว
                            LOGGER.debug("Config สาขาชั่วคราว");
                        }else{
                            empInternal.setTempBranch("N");
                            empInternal.setDepartmentForLead(departmentForLead);
                        }
                        empRepo.save(empInternal);
                    }
                    LOGGER.debug("===========================================================");
                }
            }
            if (!usernameActive.isEmpty()) {
                //Set Appuser to Status R
                LOGGER.debug("usernameActive Size {}", usernameActive.size());
                List<AppUser> userNotActive = appUserRepositoryCustom.updateStatusRetire(usernameActive);
                LOGGER.debug("userNotActive Size {}", userNotActive);
                for (AppUser appUser : userNotActive) {
                    appUser.setStatus("R"); //set Retire
                    appUserRepository.save(appUser);
                }
            }
            LOGGER.debug("Success update Employee !!");
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }
}

