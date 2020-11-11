package com.app2.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Component
public class DateUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static final SimpleDateFormat DATE_FORMAT_WITH_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static String[] monthThai = new String[]{
            "มกราคม", "กุมภาพันธ์", "มีนาคม",
            "เมษายม", "พฤษภาคม", "มิถุนายน",
            "กรกฎาคม", "สิงหาคม", "กันยายน",
            "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    };

    public static Timestamp getCurrentDate() {
        Timestamp today = null;
        try {
            Date nowDate = Calendar.getInstance().getTime();
            today = new Timestamp(nowDate.getTime());
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return today;
    }

    public static Timestamp getTimeStamp(String stringDate) {
        Timestamp today = null;
        try {
            today = getDateWithRemoveTime(DATE_FORMAT.parse(stringDate));
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }

        return today;
    }

    public static Timestamp getTimeStampGetMaxTime(String stringDate) {
        Timestamp today = null;
        try {
            today = getTimeMax(DATE_FORMAT.parse(stringDate));
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return today;
    }


    public static Locale getSystemLocale() {
        return Locale.US;
    }

    public static Timestamp getDateWithRemoveTime(Date date) {
        LOGGER.info("getDateWithRemoveTime : {} ", date);
        Timestamp maxTimeDate = null;
        try {
            SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
            maxTimeDate = Timestamp.valueOf(newformat.format(date) + " " + "00:00:00.000");
            LOGGER.debug("getDateWithRemoveTime return : {}", maxTimeDate);
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return maxTimeDate;
    }


    public static Timestamp getTimeMax(Date date) {
        LOGGER.info("getDateWithRemoveTime : {} ", date);
        Timestamp maxTimeDate = null;
        try {
            SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
            maxTimeDate = Timestamp.valueOf(newformat.format(date) + " " + "23:59:59.999");
            LOGGER.debug("getDateWithRemoveTime return : {}", maxTimeDate);
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return maxTimeDate;
    }


    public static Timestamp getDateWithMaxTime(String date) {
        LOGGER.info("getDateWithMaxTime : {} ", date);
        Timestamp minTimeDate = null;
        try {
            String newFormateDate = convertStringDate(date);
            minTimeDate = Timestamp.valueOf(newFormateDate + " " + "23:59:59.999");

        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
        return minTimeDate;
    }

    private static String convertStringDate(String dateString1) {
        String newDate = "";
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", DateUtil.getSystemLocale()).parse(dateString1);
            SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
            newDate = newformat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public static String convertStringDateToString(String dateString) {
        String newDate = "";
        try {
            Date date = new SimpleDateFormat("yyyyMMdd", DateUtil.getSystemLocale()).parse(dateString);
            SimpleDateFormat newformat = new SimpleDateFormat("yyyy-MM-dd", DateUtil.getSystemLocale());
            newDate = newformat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String convertStringDateTimeToString(String dateString) {
        String newDate = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateUtil.getSystemLocale()).parse(dateString);
            SimpleDateFormat newformat = new SimpleDateFormat("yyyyMMdd", DateUtil.getSystemLocale());
            newDate = newformat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String convertDateToString(Date dateIn) {
        String newDate = "";
        try {
            newDate = DATE_FORMAT_WITH_TIME.format(dateIn);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
        }
        return newDate;
    }

    public static String convertDateToString(Date dateIn, String format) {
        String newDate = "";
        try {
            SimpleDateFormat newformat = new SimpleDateFormat(format, DateUtil.getSystemLocale());
            newDate = newformat.format(dateIn);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
        }
        return newDate;
    }

    public static Date getLongToDate(Long dateLong) {
        Date date = new Date(dateLong);
        return date;
    }

    public static Date getLongToDate(String longStr) {
        Double timeStamp = Double.parseDouble(longStr);
        Date date = new Date(timeStamp.longValue());
        return date;
    }


    public static String ConvertDateExcelDateFormat(String stringDate) {
        try {
            String dateStr = stringDate;
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            Date date = (Date) formatter.parse(dateStr);
            System.out.println(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String formatedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
            System.out.println("formatedDate : " + formatedDate);
            return formatedDate;
        } catch (Exception e) {
            LOGGER.error("error msg : {} ", e);
            throw new RuntimeException(e);
        }
    }

    public static String checkDateFormat(String str) {
        if (str.length() > 10) {
            String itemSplit[] = str.split("\\-");


            String day = itemSplit[2].substring(0, 2);
            String month = itemSplit[1];
            String year = itemSplit[0];

            StringBuilder sb = new StringBuilder();
            sb.append(day);
            sb.append("-");
            sb.append(month);
            sb.append("-");
            sb.append(year);


            return sb.toString();
        } else {
            return str;
        }
    }

    public static String getDateStringThai(Date date) {
        return date.getDate() + " " + monthThai[date.getMonth()] + " " + (date.getYear() + 1900 + 543);
    }

    public static String codeCurrentDate(){
        String pattern = "yyyy-MM-dd";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        String currentDate = dateFormat.format(date);
        String[] currentDateAr = currentDate.split("-");
        String codeDate = currentDateAr[0]+currentDateAr[1]+currentDateAr[2];
        return codeDate;
    }
}


