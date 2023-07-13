package ru.netology.diplom.utils;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Locale;

public class CardUtil {
    private static final String FIRST_CARD_NUMBER = "4444444444444441";
    private static final String SECOND_CARD_NUMBER = "4444444444444442";
    private static final Faker FAKER_EN = new Faker();
    private static final Faker FAKER_RU = new Faker(new Locale("ru"));
    private static final String SYMBOLS = " `~!@#$%^&*()_+-=№;:?[]{}/<>\\,.|";

    public static Card getFirstCard() {
        return getCardWithNumber(FIRST_CARD_NUMBER);
    }

    public static Card getSecondCard() {
        return getCardWithNumber(SECOND_CARD_NUMBER);
    }

    public static Card getCardWithEmptyNumber() {
        return getCardWithNumber("");
    }

    public static Card getCardWithNumberLessThen16Digits() {
        return getCardWithNumber("444444444444444");
    }

    public static Card getCardWithNumberMoreThen16Digits() {
        return getCardWithNumber("11112222333344445");
    }

    public static Card getCardWithNumberHasLetter() {
        return getCardWithNumber(FAKER_EN.letterify("444444444444444?"));
    }

    public static Card getCardWithNumberHasSymbol() {
        return getCardWithNumber("444444444444444" + getRandomSymbols(1));
    }

    public static Card getFirstCardWithEmptyMonth() {
        return getCardWithMonth("");
    }

    public static Card getFirstCardWithMonthZero() {
        return getCardWithMonth("0");
    }

    public static Card getFirstCardWithMonthDoubleZero() {
        return getCardWithMonth("00");
    }

    public static Card getFirstCardWithMonthHas1Digit() {
        return getCardWithMonth(String.valueOf(FAKER_EN.number().numberBetween(1, 9)));
    }

    public static Card getFirstCardWithMonthHas3Digits() {
        return getCardWithMonth("007");
    }

    public static Card getFirstCardWithMonthMoreThen12() {
        return getCardWithMonth(String.valueOf(FAKER_EN.number().numberBetween(13, 99)));
    }

    public static Card getFirstCardWithMonthHasLetters() {
        return getCardWithMonth(FAKER_EN.letterify("??", true));
    }

    public static Card getFirstCardWithMonthHasSymbols() {
        return getCardWithMonth(getRandomSymbols(2));
    }

    public static Card getFirstCardWithNegativeMonth() {
        return getCardWithMonth("-" + DateUtils.getNextMonth());
    }

    public static Card getFirstCardWithEmptyYear() {
        return getCardWithYear("");
    }

    public static Card getFirstCardWithYearHas1Digit() {
        return getCardWithYear(FAKER_EN.number().digit());
    }

    public static Card getFirstCardWithYearHas3Digits() {
        return getCardWithYear(DateUtils.getNextYearAs3Digits());
    }

    public static Card getFirstCardWithYearHas4Digits() {
        return getCardWithYear(DateUtils.getNextYearAs4Digits());
    }

    public static Card getFirstCardWithYearHasLetter() {
        return getCardWithYear(FAKER_EN.letterify("??", true));
    }

    public static Card getFirstCardWithYearHasSymbol() {
        return getCardWithYear(getRandomSymbols(2));
    }

    public static Card getFirstCardWithNegativeYear() {
        return getCardWithMonth("-" + DateUtils.getNextYear());
    }
    
    private static String getRandomSymbols(int number) {
        String result = "";
        for (int i = 0; i < number; i++) {
            result = result + SYMBOLS.charAt(FAKER_EN.number().numberBetween(0, SYMBOLS.length()));
        }
        return result;
    }

    public static Card getFirstCardWithEmptyHolder() {
        return getCardWithHolder("");
    }

    public static Card getFirstCardWithHolderLastName() {
        return getCardWithHolder(FAKER_EN.name().lastName().toUpperCase(Locale.ENGLISH));
    }

    public static Card getFirstCardWithHolderLastNameCyrillic() {
        return getCardWithHolder(FAKER_RU.name().lastName().toUpperCase(new Locale("ru")));
    }

    public static Card getFirstCardWithHolderFullName() {
        return getCardWithHolder(FAKER_EN.name().nameWithMiddle().toUpperCase(Locale.ENGLISH));
    }

    public static Card getFirstCardWithHolderFullNameCyrillic() {
        return getCardWithHolder(FAKER_RU.name().nameWithMiddle().toUpperCase(new Locale("ru")));
    }

    public static Card getFirstCardWithHolderNameCyrillic() {
        return getCardWithHolder(FAKER_RU.name().fullName().toUpperCase(new Locale("ru")));
    }

    public static Card getFirstCardWithHolderNameHas1SymbolicWord() {
        return getCardWithHolder(getRandomSymbols(1));
    }

    public static Card getFirstCardWithHolderNameHas2SymbolicWords() {
        return getCardWithHolder(getRandomSymbols(1) + " " + getRandomSymbols(1));
    }

    public static Card getFirstCardWithHolderNameHasDigits() {
        return getCardWithHolder(FAKER_EN.numerify("### ###"));
    }

    public static Card getFirstCardWithEmptyCode() {
        return getCardWithCode("");
    }

    public static Card getFirstCardWithEmptyCodeHas1Digit() {
        return getCardWithCode(FAKER_EN.numerify("#"));
    }

    public static Card getFirstCardWithEmptyCodeHas2Digit() {
        return getCardWithCode(FAKER_EN.numerify("##"));
    }

    public static Card getFirstCardWithEmptyCodeHas4Digit() {
        return getCardWithCode(FAKER_EN.numerify("####"));
    }

    public static Card getFirstCardWithEmptyCodeHasLetters() {
        return getCardWithCode(FAKER_EN.letterify("???"));
    }

    public static Card getFirstCardWithEmptyCodeHasSymbols() {
        return getCardWithCode(getRandomSymbols(3));
    }

    public static Card getFirstCardWithLastMonth() {
        LocalDate lastMonth = DateUtils.getLastMonthDate();
        return getCardWithDate(DateUtils.getMonth(lastMonth.getMonthValue()), DateUtils.getYear(lastMonth.getYear()));
    }

    public static Card getFirstCardWithLastYear() {
        LocalDate lastMonth = DateUtils.getLastYearDate();
        return new Card(
                FIRST_CARD_NUMBER,
                DateUtils.getMonth(lastMonth.getMonthValue()),
                DateUtils.getYear(lastMonth.getYear()),
                FAKER_EN.name().fullName(),
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithNumber(String number) {
        return new Card(
                number,
                DateUtils.getNextMonth(),
                DateUtils.getNextYear(),
                FAKER_EN.name().fullName(),
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithMonth(String month) {
        return new Card(
                FIRST_CARD_NUMBER,
                month,
                DateUtils.getNextYear(),
                FAKER_EN.name().fullName(),
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithYear(String year) {
        return new Card(
                FIRST_CARD_NUMBER,
                DateUtils.getNextMonth(),
                year,
                FAKER_EN.name().fullName(),
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithDate(String month, String year) {
        return new Card(
                FIRST_CARD_NUMBER,
                month,
                year,
                FAKER_EN.name().fullName(),
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithHolder(String holder) {
        return new Card(
                FIRST_CARD_NUMBER,
                DateUtils.getNextMonth(),
                DateUtils.getNextYear(),
                holder,
                FAKER_EN.number().digits(3)
        );
    }

    private static Card getCardWithCode(String code) {
        return new Card(
                FIRST_CARD_NUMBER,
                DateUtils.getNextMonth(),
                DateUtils.getNextYear(),
                FAKER_EN.name().fullName(),
                code
        );
    }

}
