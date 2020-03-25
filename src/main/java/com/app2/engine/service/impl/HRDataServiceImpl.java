package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Area;
import com.app2.engine.entity.app.Position;
import com.app2.engine.entity.app.Zone;
import com.app2.engine.repository.AreaRepository;
import com.app2.engine.repository.PositionRepository;
import com.app2.engine.repository.ZoneRepository;
import com.app2.engine.service.HRDataService;
import com.app2.engine.service.SmbFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
    SmbFileService smbFileService;

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

            String delimeter = "\\|";
            int length = 2;
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                String lineArr[] = line.split(delimeter);
                if (lineArr.length >= length) {
                    String code = lineArr[0];
                    String desc = lineArr[1];
                    List<Zone> zones = zoneRepository.findByCode(code);
                    if (zones.size() == 0) {
                        Zone zone = new Zone();
                        zone.setStatus("Y");
                        zone.setCode(code);
                        zone.setName(desc);
                        zoneRepository.save(zone);
                    } else {
                        for (Zone zone : zones) {
                            zone.setName(desc);
                            zoneRepository.save(zone);
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
    public void section() {
        try {
            String fileName = "HRSECTION.TXT";
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
}
