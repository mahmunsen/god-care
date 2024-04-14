package com.godcare.api.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final String yearMonthDayHourMinuteSecond = "yyyy-MM-dd HH:mm:ss";

    // String -> Instant
    public static Instant convertToInstant(String inputDate, String pattern) {

        // 원하는 시간 패턴 설정
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(pattern);
        // LocalDateTime에 인스턴트 타입을 DateTimeFormatter 형식 사용해 파싱
        LocalDateTime date = LocalDateTime.parse(inputDate, inputFormat);
        // LocalDateTime을 ZoneId.systemDefault() 으로 변환 -> 시스템의 기본 시간대(한국 시간)이용
        return date.atZone(ZoneId.systemDefault()).toInstant();
    }

    // Instant -> String
    public static String convertToString(Instant inputDate, String pattern) {
        // 원하는 시간 패턴 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(inputDate.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
}
