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

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonth().getValue();
    }
}
