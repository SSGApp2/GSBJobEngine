package com.app2.engine;

import com.app2.engine.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
public class TestCreateFileBatchName {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testFileAd() {
        String fileName = "AD_20200525.csv";
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String fileNames = "AD_" + timeLog + ".csv";
        LOGGER.debug("fileName  {}", fileNames);
    }

    @Test
    public void updateControlDep() {

        String delimeter = "\\|";
        String line = "1000000010000001100000541000005600000000000000001000006100000000|10000000|10000001|10000054|10000056|00000000|00000000|10000061|00000000|0000009572|";
        String lineArr[] = line.split(delimeter);
        String code = null, company = null, orgGroup = null, lineBusiness = null, zone = null, area = null, branch = null, unit = null, subUnit = null;
        Map<String, String> map = new HashMap();
        for (int i = 1; i <= 8; i++) {
            String value = lineArr[i];
            if (!value.equals("00000000")) {
                map.put(value, i + "");
            }
        }
        map = this.sortMap(map); //sort map
        LOGGER.debug("Map {}", map);
        List<Map> mapList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
//            LOGGER.debug("Key: " + entry.getKey());
//            LOGGER.debug("Value: " + entry.getValue());
            Map mp = new HashMap();
            mp.put("code", entry.getKey());
            mp.put("index", entry.getValue());
            mapList.add(mp);
        }
        String department = null, controlDept = null, type = null;

        if (mapList.size() == 1) {
            //is Head
            Map dept = mapList.get(0);
            department = (String) dept.get("code");
            type = (String) dept.get("index");

        } else if (mapList.size() > 1) {
            //is sub department
            Map dept = mapList.get(0);
            department = (String) dept.get("code");
            type = (String) dept.get("index");

            Map deptHead = mapList.get(1);
            controlDept = (String) deptHead.get("code");
        }
        String [] typeCode={"","company","orgGroup","lineBusiness","zone","area","branch","unit","subUnit"};
        Integer numType=Integer.parseInt(type);
        LOGGER.debug("=================================================");
        LOGGER.debug("Department {}",department);
        LOGGER.debug("type {} code {}",type,typeCode[numType]);
        LOGGER.debug("controlDept {}",controlDept);
        LOGGER.debug("=================================================");
    }

    private LinkedHashMap<String, String> sortMap(Map<String, String> map) { //desc
        List<Map.Entry<String, String>> capitalList = new LinkedList<>(map.entrySet());
        Collections.sort(capitalList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : capitalList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}


