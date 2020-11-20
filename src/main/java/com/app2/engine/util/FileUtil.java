package com.app2.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtil {

    public static String isNotExistsDirCreated(String directory,String date) {
        String dirWithDay = directory + "/" + date;
        File dir = new File(dirWithDay);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirWithDay;
    }
}
