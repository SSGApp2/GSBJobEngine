package com.app2.engine;

import com.app2.engine.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestGetDate {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testGetCodeDateBeforeOneDay() {
        LOGGER.debug("CodeDateBeforeOneDay :  {}", DateUtil.codeCurrentDateBeforeOneDay());
    }

}


