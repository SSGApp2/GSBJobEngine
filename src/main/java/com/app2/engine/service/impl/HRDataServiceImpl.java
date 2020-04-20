package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.AreaMapBranchRepositoryCustom;
import com.app2.engine.repository.custom.ZoneMapAreaRepositoryCustom;
import com.app2.engine.service.HRDataService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class HRDataServiceImpl implements HRDataService {
    private Logger LOGGER = LoggerFactory.getLogger(HRDataServiceImpl.class);

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    LineBusinessRepository lineBusinessRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    OrgGroupRepository orgGroupRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    HrInterfaceRepository hrInterfaceRepository;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ZoneMapAreaRepository zoneMapAreaRepository;

    @Autowired
    ZoneMapAreaRepositoryCustom zoneMapAreaRepositoryCustom;

    @Autowired
    AreaMapBranchRepository areaMapBranchRepository;

    @Autowired
    AreaMapBranchRepositoryCustom areaMapBranchRepositoryCustom;

    @Override
    @Transactional
    public void region() {
        //update รหัสภาค/ฝ่าย
        try {
            String fileName = "HRREGION.TXT";
            smbFileService.copyRemoteFileToLocalFile(fileName);
//            String pathName = "/home/thongchai/Documents/GSB/HRDATA/HRREGION.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);
            List<String> codeList = new ArrayList<>();

            String delimeter = "\\|";
            int length = 2;
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    String subCode = code.substring(0,2);
                    String zoneType = null;
                    if (subCode.equals("10")){
                        zoneType = "1";
                    } else if (subCode.equals("15")){
                        zoneType = "2";
                    }
                    codeList.add(code);
                    List<Zone> zones = zoneRepository.findByCode(code);
                    if (zones.size() == 0) {
                        Zone zone = new Zone();
                        zone.setStatus("Y");
                        zone.setCode(code);
                        zone.setName(desc);
                        zone.setZoneType(zoneType);
                        zoneRepository.save(zone);
                    } else {
                        for (Zone zone : zones) {
                            zone.setName(desc);
                            zone.setZoneType(zoneType);
                            zoneRepository.save(zone);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<Zone> zones = zoneRepository.findByCodeNotIn(codeList);
            for (Zone zone : zones){
                zone.setStatus("N");
                zoneRepository.save(zone);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void section() {
        try {
            String fileName = "HRSECTION.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);
            List<String> codeList = new ArrayList<>();

            String delimeter = "\\|";
            int length = 2;
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    codeList.add(code);
                    List<Area> areas = areaRepository.findByCode(code);
                    if (areas.size() == 0) {
                        Area area = new Area();
                        area.setStatus("Y");
                        area.setCode(code);
                        area.setName(desc);
                        areaRepository.save(area);
                    } else {
                        for (Area area : areas) {
                            area.setName(desc);
                            areaRepository.save(area);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<Area> areas = areaRepository.findByCodeNotIn(codeList);
            for (Area area : areas){
                area.setStatus("N");
                areaRepository.save(area);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }


    @Override
    @Transactional
    public void position() {
        try {
            String fileName = "HRPOSITION.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);

            String delimeter = "\\|";
            int length = 3;
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    String subCode = lineArr[2];
                    List<Position> positions = positionRepository.findByCodeAndSubCode(code, subCode);
                    if (positions.size() == 0) {
                        Position position = new Position();
                        position.setCode(code);
                        position.setSubCode(subCode);
                        position.setName(desc);
                        positionRepository.save(position);
                    } else {
                        for (Position position : positions) {
                            position.setName(desc);
                            positionRepository.save(position);
                        }
                    }
                    LOGGER.debug(line);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void branch() {
        try {
            String fileName = "HRBRANCH.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);

            String delimeter = "\\|";
            int length = 2;
            List<String> codeList = new ArrayList<>();
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    String subCode = code.substring(0,2);
                    String branchType = null;
                    if (subCode.equals("10")){
                        branchType = "1";
                    } else if (subCode.equals("15")){
                        branchType = "2";
                    }
                    codeList.add(code);
                    List<Branch> branches = branchRepository.findByCode(code);
                    if (branches.size() == 0) {
                        Branch branch = new Branch();
                        branch.setCode(code);
                        branch.setName(desc);
                        branch.setStatus("Y");
                        branch.setBranchType(branchType);
                        branchRepository.save(branch);
                    } else {
                        for (Branch branch : branches) {
                            branch.setName(desc);
                            branch.setBranchType(branchType);
                            branchRepository.save(branch);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<Branch> branches = branchRepository.findByCodeNotIn(codeList);
            for (Branch branch : branches){
                branch.setStatus("N");
                branchRepository.save(branch);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void lineBusiness() {
        try {
            String fileName = "HRDIV.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);
            List<String> codeList = new ArrayList<>();

            String delimeter = "\\|";
            int length = 2;
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    codeList.add(code);
                    List<LineBusiness> lineBusinesses = lineBusinessRepository.findByCode(code);
                    if (lineBusinesses.size() == 0) {
                        LineBusiness lineBusiness = new LineBusiness();
                        lineBusiness.setCode(code);
                        lineBusiness.setName(desc);
                        lineBusiness.setStatus("Y");
                        lineBusinessRepository.save(lineBusiness);
                    } else {
                        for (LineBusiness lineBusiness : lineBusinesses) {
                            lineBusiness.setName(desc);
                            lineBusinessRepository.save(lineBusiness);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<LineBusiness> lineBusinesses = lineBusinessRepository.findByCodeNotIn(codeList);
            for (LineBusiness lineBusiness : lineBusinesses){
                lineBusiness.setStatus("N");
                lineBusinessRepository.save(lineBusiness);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void unit() {
        try {
            List<String> codeList = new ArrayList<>();
            for (int i=0;i<2;i++){
                String fileName;
                if (i == 0){
                    fileName = "HRDEPT.TXT"; // หน่วย
                } else {
                    fileName = "HRUNIT.TXT"; // หน่วยย่อย
                }
                String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
                InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
                BufferedReader bfReader = new BufferedReader(streamReader);

                String delimeter = "\\|";
                int length = 2;
                while (bfReader.ready()) {
                    String line = bfReader.readLine();
                    String lineArr[] = line.split(delimeter);
                    if (lineArr.length >= length) {
                        String code = lineArr[0];
                        String desc = lineArr[1];
                        codeList.add(code);
                        List<Unit> units = unitRepository.findByCode(code);
                        if (units.size() == 0) {
                            Unit unit = new Unit();
                            unit.setCode(code);
                            unit.setName(desc);
                            unit.setStatus("Y");
                            unitRepository.save(unit);
                        } else {
                            for (Unit unit : units) {
                                unit.setName(desc);
                                unitRepository.save(unit);
                            }
                        }
                        LOGGER.debug(line);
                    }
                }
            }

            List<Unit> units = unitRepository.findByCodeNotIn(codeList);
            for (Unit unit : units){
                unit.setStatus("N");
                unitRepository.save(unit);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void orgGroup() {
        try {
            String fileName = "HRBUSILINE.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);

            String delimeter = "\\|";
            int length = 2;
            List<String> codeList = new ArrayList<>();
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    codeList.add(code);
                    List<OrgGroup> orgGroups = orgGroupRepository.findByCode(code);
                    if (orgGroups.size() == 0) {
                        OrgGroup orgGroup = new OrgGroup();
                        orgGroup.setCode(code);
                        orgGroup.setName(desc);
                        orgGroup.setStatus("Y");
                        orgGroupRepository.save(orgGroup);
                    } else {
                        for (OrgGroup orgGroup : orgGroups) {
                            orgGroup.setName(desc);
                            orgGroupRepository.save(orgGroup);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<OrgGroup> orgGroups = orgGroupRepository.findByCodeNotIn(codeList);
            for (OrgGroup orgGroup : orgGroups){
                orgGroup.setStatus("N");
                orgGroupRepository.save(orgGroup);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void company() {
        try {
            String fileName = "HRMAINSTR.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);

            String delimeter = "\\|";
            int length = 2;
            List<String> codeList = new ArrayList<>();
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    codeList.add(code);
                    List<Company> companies = companyRepository.findByCode(code);
                    if (companies.size() == 0) {
                        Company company = new Company();
                        company.setCode(code);
                        company.setName(desc);
                        company.setStatus("Y");
                        companyRepository.save(company);
                    } else {
                        for (Company company : companies) {
                            company.setName(desc);
                            companyRepository.save(company);
                        }
                    }
                    LOGGER.debug(line);
                }
            }

            List<Company> companies = companyRepository.findByCodeNotIn(codeList);
            for (Company company : companies){
                company.setStatus("N");
                companyRepository.save(company);
            }
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void hrInterface(){
        try {
            String fileName = "HRCOMPANYREL.TXT";
            String pathName = smbFileService.copyRemoteFileToLocalFile(fileName);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(pathName), "UTF-8");
            BufferedReader bfReader = new BufferedReader(streamReader);

            String delimeter = "\\|";
            int count = 0;
            while (bfReader.ready()) {
                count++;
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                String code = null;
                String company = null;
                String orgGroup = null;
                String lineBusiness = null;
                String zone = null;
                String area = null;
                String branch = null;
                String unit = null;
                String subUnit = null;
                String costCenter = null;
                String description3level = null;
                String description1level = null;
                String affiliation = null;
                String rank = null;
                String subRank = null;
                String address1 = null;
                String address2 = null;
                String district = null;
                String province = null;
                String postcode = null;
                String telephoneNumber = null;
                String boundary = null;
                String subBoundary = null;
                String oldDept = null;

                if (lineArr.length > 1) {
                    for (int i = 0; i < lineArr.length; i++) {
                        switch (i) {
                            case 0:
                                code = lineArr[i];
                                break;
                            case 1:
                                company = lineArr[i];
                                break;
                            case 2:
                                orgGroup = lineArr[i];
                                break;
                            case 3:
                                lineBusiness = lineArr[i];
                                break;
                            case 4:
                                zone = lineArr[i];
                                break;
                            case 5:
                                area = lineArr[i];
                                break;
                            case 6:
                                branch = lineArr[i];
                                break;
                            case 7:
                                unit = lineArr[i];
                                break;
                            case 8:
                                subUnit = lineArr[i];
                                break;
                            case 9:
                                costCenter = lineArr[i];
                                break;
                            case 10:
                                description3level = lineArr[i];
                                break;
                            case 11:
                                description1level = lineArr[i];
                                break;
                            case 12:
                                affiliation = lineArr[i];
                                break;
                            case 13:
                                rank = lineArr[i];
                                break;
                            case 14:
                                subRank = lineArr[i];
                                break;
                            case 15:
                                address1 = lineArr[i];
                                break;
                            case 16:
                                address2 = lineArr[i];
                                break;
                            case 17:
                                district = lineArr[i];
                                break;
                            case 18:
                                province = lineArr[i];
                                break;
                            case 19:
                                postcode = lineArr[i];
                                break;
                            case 20:
                                telephoneNumber = lineArr[i];
                                break;
                            case 21:
                                boundary = lineArr[i];
                                break;
                            case 22:
                                subBoundary = lineArr[i];
                                break;
                            case 23:
                                oldDept = lineArr[i];
                                break;
                        }
                    }

                    List<HrInterface> hrInterfaces = hrInterfaceRepository.findByCode(code);
                    if (hrInterfaces.size() == 0) {
                        HrInterface hrInterface = new HrInterface();
                        hrInterface.setCode(code);
                        hrInterface.setCompany(company);
                        hrInterface.setOrgGroup(orgGroup);
                        hrInterface.setLineBusiness(lineBusiness);
                        hrInterface.setZone(zone);
                        hrInterface.setArea(area);
                        hrInterface.setBranch(branch);
                        hrInterface.setUnit(unit);
                        hrInterface.setSubUnit(subUnit);
                        hrInterface.setCostCenter(costCenter);
                        hrInterface.setDescription3level(description3level);
                        hrInterface.setDescription1level(description1level);
                        hrInterface.setAffiliation(affiliation);
                        hrInterface.setRank(rank);
                        hrInterface.setSubRank(subRank);
                        hrInterface.setAddress1(address1);
                        hrInterface.setAddress2(address2);
                        hrInterface.setDistrict(district);
                        hrInterface.setProvince(province);
                        hrInterface.setPostcode(postcode);
                        hrInterface.setTelephoneNumber(telephoneNumber);
                        hrInterface.setBoundary(boundary);
                        hrInterface.setSubBoundary(subBoundary);
                        hrInterface.setOldDept(oldDept);
                        hrInterfaceRepository.save(hrInterface);
                    } else {
                        for (HrInterface hrInterface : hrInterfaces) {
                            hrInterface.setCompany(company);
                            hrInterface.setOrgGroup(orgGroup);
                            hrInterface.setLineBusiness(lineBusiness);
                            hrInterface.setZone(zone);
                            hrInterface.setArea(area);
                            hrInterface.setBranch(branch);
                            hrInterface.setUnit(unit);
                            hrInterface.setSubUnit(subUnit);
                            hrInterface.setCostCenter(costCenter);
                            hrInterface.setDescription3level(description3level);
                            hrInterface.setDescription1level(description1level);
                            hrInterface.setAffiliation(affiliation);
                            hrInterface.setRank(rank);
                            hrInterface.setSubRank(subRank);
                            hrInterface.setAddress1(address1);
                            hrInterface.setAddress2(address2);
                            hrInterface.setDistrict(district);
                            hrInterface.setProvince(province);
                            hrInterface.setPostcode(postcode);
                            hrInterface.setTelephoneNumber(telephoneNumber);
                            hrInterface.setBoundary(boundary);
                            hrInterface.setSubBoundary(subBoundary);
                            hrInterface.setOldDept(oldDept);
                            hrInterfaceRepository.save(hrInterface);
                        }
                    }
                    LOGGER.debug(line);

                    // get LineBusiness
                    LineBusiness lineBusiness1 = null;
                    if (AppUtil.isNotEmpty(lineBusiness)){
                        List<LineBusiness> lineBusinesses = lineBusinessRepository.findByCode(lineBusiness);
                        if (lineBusinesses.size() > 0){
                            lineBusiness1 = lineBusinesses.get(0);
                        }
                    }

                    // get Zone
                    Zone zone1 = null;
                    if (AppUtil.isNotEmpty(zone)){
                        List<Zone> zones = zoneRepository.findByCode(zone);
                        if (zones.size() > 0){
                            zone1 = zones.get(0);
                        }
                    }

                    // get Arae
                    Area area1 = null;
                    if (AppUtil.isNotEmpty(area)){
                        List<Area> areas = areaRepository.findByCode(area);
                        if (areas.size() > 0){
                            area1 = areas.get(0);
                        }
                    }

                    // get Branch
                    Branch branch1 = null;
                    if (AppUtil.isNotEmpty(branch)){
                        List<Branch> branches = branchRepository.findByCode(branch);
                        if (branches.size() > 0){
                            branch1 = branches.get(0);
                        }
                    }

                    // get Unit
                    Unit unit1 = null;
                    if (AppUtil.isNotEmpty(unit)){
                        List<Unit> units = unitRepository.findByCode(unit);
                        if (units.size() > 0){
                            unit1 = units.get(0);
                        }
                    }

                    // get Sub Unit
                    Unit subUnit1 = null;
                    if (AppUtil.isNotEmpty(subUnit)){
                        List<Unit> subUnits = unitRepository.findByCode(subUnit);
                        if (subUnits.size() > 0){
                            subUnit1 = subUnits.get(0);
                        }
                    }

                    // set LineBusiness of zone
                    if (AppUtil.isNotNull(zone1)){
                        zone1.setLineBusiness(lineBusiness1);
                        zoneRepository.save(zone1);
                    }

                    // set zone map area
                    if (AppUtil.isNotEmpty(zone1) && AppUtil.isNotEmpty(area1)) {
                        List<ZoneMapArea> zoneMapAreas = zoneMapAreaRepositoryCustom.findByZoneCodeAndAreaCode(zone, area);
                        if (zoneMapAreas.size() == 0) {
                            ZoneMapArea zoneMapArea = new ZoneMapArea();
                            zoneMapArea.setZone(zone1);
                            zoneMapArea.setArea(area1);
                            zoneMapAreaRepository.save(zoneMapArea);
                        }
                    }

                    // set area map branch
                    if (AppUtil.isNotEmpty(area1) && AppUtil.isNotEmpty(branch1)) {
                        List<AreaMapBranch> areaMapBranches = areaMapBranchRepositoryCustom.fineByAreaCodeAndBranchCode(area, branch);
                        if (areaMapBranches.size() == 0) {
                            AreaMapBranch areaMapBranch = new AreaMapBranch();
                            areaMapBranch.setArea(area1);
                            areaMapBranch.setBranch(branch1);
                        }
                    }

                    // set phone number of branch
                    if (AppUtil.isNotEmpty(branch1)){
                        branch1.setPhoneNumber(telephoneNumber);
                        branchRepository.save(branch1);
                    }

                    // set branch of unit
                    if (AppUtil.isNotEmpty(unit1)){
                        unit1.setBranch(branch1);
                        unitRepository.save(unit1);
                    }

                    // set parent of unit
                    if (AppUtil.isNotEmpty(subUnit1)){
                        if (AppUtil.isNotNull(unit1)){
                            subUnit1.setUnitParent(unit1.getId());
                            unitRepository.save(subUnit1);
                        }

                    }
                }
            }
            LOGGER.info("Total Record : {}",count);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
