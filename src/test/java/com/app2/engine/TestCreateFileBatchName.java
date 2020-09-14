package com.app2.engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RunWith(SpringRunner.class)
public class TestCreateFileBatchName {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Test
    public void testFileAd() {
        String fileName = "AD_20200525.csv";
        String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String fileNames = "AD_" + timeLog + ".csv";
        LOGGER.debug("fileName  {}",fileNames );
    }
}
