package ru.netology.diplom.utils;

import java.time.LocalDate;

public class DateUtils {

    public static String getNextYearAs3Digits() {
        LocalDate date = LocalDate.now().plusYears(1);
        int year = date.getYear();
        return String.valueOf(year).substring(1);
    }

    public static String getNextYearAs4Digits() {
        LocalDate date = LocalDate.now().plusYears(1);
        int year = date.getYear();
        return String.valueOf(year);
    }

    public static String getNextMonth() {
        int nextMonthNumber = getNextMonthDate().getMonth().getValue();
        return getMonth(nextMonthNumber);
    }

    public static String getMonth(int month) {
        return String.format("%02d", month);
    }

    public static String getNextYear() {
        int nextYear = getNextYearDate().getYear();
        return getYear(nextYear);
    }

    public static String getYear(int year) {
        return String.valueOf(year).substring(2);
    }

    private static LocalDate getNextMonthDate() {
        return LocalDate.now().plusMonths(1);
    }

    public static LocalDate getLastMonthDate() {
        return LocalDate.now().minusMonths(1);
    }

    public static LocalDate getLastYearDate() {
        return LocalDate.now().minusYears(1);
    }
    public static LocalDate getNextYearDate() {
        return LocalDate.now().plusYears(1);
    }
}
