package org.aircas.resource.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class DateTimeUtils {

  public static final String DATE_FORMAT_DATETIME_SIMPLE = "yyMMddHHmmss";
  /**
   * 年-月-日
   */
  public static final String DATE_FORMAT_DATE_ONLY = "yyyy-MM-dd";

  /**
   * 年-月-日
   */
  public static final String DATE_FORMAT_DATE_ONLY_MONTH = "yyyyMM";
  /**
   * 年-月-日 时分秒
   */
  public static final String DATETIME_FORMAT_DATE_ONLY = "yyyy-MM-dd HH:mm:ss";

  /**
   * 年-月-日 时分
   */
  public static final String DATE_FORMAT_DATE = "yyyy-MM-dd HH:mm";

  /**
   * dateTime formatter
   */
  public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_DATE_ONLY);
  /**
   * date formatter
   */
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_DATE_ONLY);

  private static final ZoneId ZONE = ZoneId.systemDefault();

  public static String formatDateTimeString(long unixTime, String format) {
    if (unixTime == 0) {
      return "";
    }
    LocalDateTime dateTime = toLocalDateTime(unixTime);
    Date date = date(dateTime);
    SimpleDateFormat formator = new SimpleDateFormat(format);
    return formator.format(date);
  }

  public static String formatDateTimeString(long unixTime) {
    return formatDateTimeString(unixTime, DATETIME_FORMAT_DATE_ONLY);
  }

  /**
   * 毫秒转为分
   *
   * @param second
   * @return
   */
  public static String getGapTime(long second) {
    if (second != 0L) {
      second = second / 1000;
    }
    //转换天数
    long days = second / 86400;
    //剩余秒数
    second = second % 86400;
    //转换小时数
    long hours = second / 3600;
    //剩余秒数
    second = second % 3600;
    //转换分钟
    long minutes = second / 60;
    //剩余秒数
    second = second % 60;
    String s = days + "天" + hours + "时" + minutes + "分" + second + "秒";
    if (0 < days) {
      return s;
    } else {
      return hours + "时" + minutes + "分" + second + "秒";
    }
  }

  /**
   * 获取String类型时间
   *
   * @return
   */
  public static String convertCalender2String() {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    Calendar calendar = Calendar.getInstance();
    return df.format(calendar.getTime());
  }

  /**
   * java.time.LocalDateTime --> java.util.Date
   *
   * @param localDateTime
   * @return
   */
  public static Date date(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    Instant instant = localDateTime.atZone(ZONE).toInstant();
    return Date.from(instant);
  }


  /**
   * 将字符串转换成日期，只到年月日
   *
   * @param str
   * @return
   */
  public static Date strToDate(String str, String format) {
    try {
      if (StringUtils.isBlank(format)) {
        format = DATETIME_FORMAT_DATE_ONLY;
      }
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
      return simpleDateFormat.parse(str);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * 将Date日期转换成String
   *
   * @param dateDate
   * @return
   */
  public static String dateToStr(Date dateDate, String formatter) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
    String dateString = simpleDateFormat.format(dateDate);
    return dateString;
  }

  /**
   * 将Date日期转换成String
   *
   * @param dateDate
   * @return
   */
  public static String dateToStr(Date dateDate) {
    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DATE_ONLY);
    String dateString = formatter.format(dateDate);
    return dateString;
  }


  public static LocalDateTime toLocalDateTime(Date date) {

    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  public static LocalDateTime toLocalDateTime(long dateLong) {

    return toLocalDateTime(new Date(dateLong));
  }

  public static Date toDate(LocalDateTime localDateTime) {

    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date toDate(LocalDate localDate) {

    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date toDate(LocalTime localTime) {

    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    return Date.from(instant);
  }

  public static long toLong(LocalDateTime localDateTime) {

    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static long toLong(LocalTime localTime) {

    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static long toLong(LocalDate localDate) {

    LocalDateTime localDateTime = localDate.atStartOfDay();
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static boolean isToday(LocalDateTime datetime) {
    LocalDateTime now = LocalDateTime.now();
    return (now.getYear() == datetime.getYear()) && (now.getMonthValue() == datetime.getMonthValue()) && (now.getDayOfMonth() == datetime.getDayOfMonth());
  }

}
