package org.aircas.orbit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtils {

  /**
   * 把时间戳转换为：时分秒
   *
   * @param millisecond ：毫秒，传入单位为毫秒
   */
  public static String formatMillSeconds(final long millisecond) {
    if (millisecond < 1000) {
      return "0" + "秒";
    }
    long second = millisecond / 1000;
    long seconds = second % 60;
    long minutes = second / 60;
    long hours = 0;
    if (minutes >= 60) {
      hours = minutes / 60;
      minutes = minutes % 60;
    }
    String timeString = "";
    String secondString = "";
    String minuteString = "";
    String hourString = "";
    if (seconds < 10) {
      secondString = "0" + seconds + "秒";
    } else {
      secondString = seconds + "秒";
    }
    if (minutes < 10 && hours < 1) {
      minuteString = minutes + "分";
    } else if (minutes < 10) {
      minuteString = "0" + minutes + "分";
    } else {
      minuteString = minutes + "分";
    }
    if (hours < 10) {
      hourString = hours + "时";
    } else {
      hourString = hours + "时";
    }
    if (hours != 0) {
      timeString = hourString + minuteString + secondString;
    } else {
      timeString = minuteString + secondString;
    }
    return timeString;
  }

  public static Date convertTimeStamp2Date(long timeStamp) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date(timeStamp);
    try {
      date = df.parse(df.format(date));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static long dateToStamp(Date date, int hour, int minute, int second) {
    LocalDateTime timestamp = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    return timestamp.withHour(hour).withMinute(minute).withSecond(second).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}
