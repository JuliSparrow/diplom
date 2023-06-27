package ru.netology.diplom.utils;

import java.time.LocalDate;

public class CardUtil {

    public static Card getFirstCard() {
        return new Card(
                "4444444444444441",
                getNextMonth(),
                getNextYear(),
                "PETROV SIDOR",
                "999"
        );
    }

    public static Card getSecondCard() {
        return new Card(
                "4444444444444442",
                getNextMonth(),
                getNextYear(),
                "PETROV SIDOR",
                "999"
        );
    }

    private static String getNextMonth() {
        int nextMonthNumber = getNextMonthDate().getMonth().getValue();
        return getMonth(nextMonthNumber);
    }

    private static String getMonth(int month) {
        return String.format("%02d", month);
    }

    private static String getNextYear() {
        int nextYear = getNextMonthDate().getYear();
        return getYear(nextYear);
    }

    private static String getYear(int year) {
        return String.valueOf(year).substring(2);
    }

    private static LocalDate getNextMonthDate() {
        return LocalDate.now().plusMonths(1);
    }

    private static LocalDate getLastMonthDate() {
        return LocalDate.now().minusMonths(1);
    }

    private static LocalDate getLastYearDate() {
        return LocalDate.now().minusYears(1);
    }
    private static LocalDate getNextYearDate() {
        return LocalDate.now().plusYears(1);
    }

    public static Card getFirstCardWithNumber(String number) {
        Card card = getFirstCard();
        card.setNumber(number);
        return card;
    }

    public static Card getFirstCardWithMonth(String month) {
        Card card = getFirstCard();
        card.setMonth(month);
        return card;
    }

    public static Card getFirstCardWithYear(String year) {
        Card card = getFirstCard();
        card.setYear(year);
        return card;
    }

    public static Card getFirstCardWithHolder(String holder) {
        Card card = getFirstCard();
        card.setHolder(holder);
        return card;
    }

    public static Card getFirstCardWithCode(String code) {
        Card card = getFirstCard();
        card.setCode(code);
        return card;
    }

    public static Card getFirstCardWithLastMonth() {
        LocalDate lastMonth = getLastMonthDate();
        Card card = getFirstCard();
        card.setMonth(getMonth(lastMonth.getMonthValue()));
        card.setYear(getYear(lastMonth.getYear()));
        return card;
    }

    public static Card getFirstCardWithLastYear() {
        LocalDate lastMonth = getLastYearDate();
        Card card = getFirstCard();
        card.setMonth(getMonth(lastMonth.getMonthValue()));
        card.setYear(getYear(lastMonth.getYear()));
        return card;
    }

    public static Card getFirstCardWithNextYear() {
        LocalDate lastMonth = getNextYearDate();
        Card card = getFirstCard();
        card.setYear(getYear(lastMonth.getYear()));
        return card;
    }
}
