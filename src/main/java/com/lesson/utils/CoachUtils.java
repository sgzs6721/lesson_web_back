package com.lesson.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 教练工具类
 */
public class CoachUtils {

    /**
     * 根据身份证号计算年龄
     *
     * @param idNumber 身份证号
     * @return 年龄
     */
    public static Integer calculateAgeFromIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() != 18) {
            return null;
        }

        try {
            // 提取出生日期（第7-14位）
            String birthDateStr = idNumber.substring(6, 14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
            
            // 计算年龄
            LocalDate now = LocalDate.now();
            Period period = Period.between(birthDate, now);
            
            return period.getYears();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据执教日期计算教龄
     *
     * @param coachingDate 执教日期
     * @return 教龄（年）
     */
    public static Integer calculateExperienceFromCoachingDate(LocalDate coachingDate) {
        if (coachingDate == null) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        Period period = Period.between(coachingDate, now);
        
        return Math.max(0, period.getYears());
    }

    /**
     * 验证身份证号格式
     *
     * @param idNumber 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() != 18) {
            return false;
        }

        // 基本格式验证
        String pattern = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        if (!idNumber.matches(pattern)) {
            return false;
        }

        // 验证出生日期是否合理
        try {
            String birthDateStr = idNumber.substring(6, 14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
            
            // 检查出生日期是否在合理范围内（1900年至今）
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate maxDate = LocalDate.now();
            
            return !birthDate.isBefore(minDate) && !birthDate.isAfter(maxDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从身份证号提取出生日期
     *
     * @param idNumber 身份证号
     * @return 出生日期
     */
    public static LocalDate extractBirthDateFromIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() != 18) {
            return null;
        }

        try {
            String birthDateStr = idNumber.substring(6, 14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(birthDateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从身份证号提取性别
     *
     * @param idNumber 身份证号
     * @return 性别代码（1-男，2-女）
     */
    public static String extractGenderFromIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() != 18) {
            return null;
        }

        // 第17位是性别码，奇数为男，偶数为女
        char genderChar = idNumber.charAt(16);
        int genderCode = Character.getNumericValue(genderChar);
        
        return genderCode % 2 == 1 ? "1" : "2";
    }
} 