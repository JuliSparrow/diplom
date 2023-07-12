package ru.netology.diplom.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.diplom.page.ShopPage;
import ru.netology.diplom.utils.Card;
import ru.netology.diplom.utils.CardUtil;

import static com.codeborne.selenide.Selenide.open;

public class BuyWithCardTest {

    @BeforeAll
    public static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Successful Pay With Card")
    void shouldSuccessfulPayWithCard() {
        var page = Selenide.open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCard();
        page.buyWithCard(card);
        page.verifyNoInvalidFields();
        page.verifySuccessNotification();
    }

    @Test
    @DisplayName("Reject Pay With Card")
    void shouldRejectPayWithCard() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getSecondCard();
        page.buyWithCard(card);
        page.verifyNoInvalidFields();
        page.verifyErrorNotification();
    }

    @Test
    @DisplayName("Validate card number (empty)")
    void shouldWarnWhenCardNumberEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithEmptyNumber();
        page.buyWithCard(card);
        page.verifyCardNumberNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (input more then 16 digits)")
    void shouldWarnWhenCardNumberHasMoreThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberMoreThen16Digits();
        page.buyWithCard(card);
        page.verifyFieldValueEquals("Номер карты", getFormattedCardNumber(card.getNumber().substring(0, 16)));
        page.verifyErrorNotification();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (input less then 16 digits)")
    void shouldWarnWhenCardNumberHasLessThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberLessThen16Digits();
        page.buyWithCard(card);
        page.verifyCardNumberInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (number has letter)")
    void shouldWarnWhenCardNumberHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberHasLetter();
        page.buyWithCard(card);
        page.verifyCardNumberInvalid();
        page.verifyFieldValueEquals("Номер карты", "4444 4444 4444 444");
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (number has symbol)")
    void shouldWarnWhenCardNumberHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberHasSymbol();
        page.buyWithCard(card);
        page.verifyCardNumberInvalid();
        page.verifyFieldValueEquals("Номер карты", "4444 4444 4444 444");
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (empty)")
    void shouldWarnWhenMonthIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyMonth();
        page.buyWithCard(card);
        page.verifyMonthNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 0)")
    void shouldWarnWhenMonthIsZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthZero();
        page.buyWithCard(card);
        page.verifyMonthInvalidFormat();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 00)")
    void shouldWarnWhenMonthIsDoubleZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthDoubleZero();
        page.buyWithCard(card);
        page.verifyMonthInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 7)")
    void shouldWarnWhenMonthHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHas1Digit();
        page.buyWithCard(card);
        page.verifyMonthInvalidFormat();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 007)")
    void shouldWarnWhenMonthHas3Digit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHas3Digits();
        page.buyWithCard(card);
        page.verifyFieldValueEquals("Месяц", "00");
        page.verifyMonthInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input month more then 12)")
    void shouldWarnWhenMonthNotExists() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthMoreThen12();
        page.buyWithCard(card);
        page.verifyMonthInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input negative value)")
    void shouldWarnWhenMonthIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithNegativeMonth();
        page.buyWithCard(card);
        page.verifySuccessNotification();
        page.verifyFieldValueEquals("Месяц", card.getMonth().replace("-", ""));
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate month (has letters)")
    void shouldWarnWhenMonthHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHasLetters();
        page.buyWithCard(card);
        page.verifyMonthNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (has symbol)")
    void shouldWarnWhenMonthHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHasSymbols();
        page.buyWithCard(card);
        page.verifyMonthNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (empty)")
    void shouldWarnWhenYearIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyYear();
        page.buyWithCard(card);
        page.verifyYearNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (has letter)")
    void shouldWarnWhenYearHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHasLetter();
        page.buyWithCard(card);
        page.verifyYearNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (has symbol)")
    void shouldWarnWhenYearHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHasSymbol();
        page.buyWithCard(card);
        page.verifyYearNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (input negative value)")
    void shouldWarnWhenYearIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithNegativeYear();
        page.buyWithCard(card);
        page.verifySuccessNotification();
        page.verifyNoInvalidFields();
        page.verifyFieldValueEquals("Год", card.getYear().replace("-", ""));
    }

    @Test
    @DisplayName("Validate month (input 4 digits of next year)")
    void shouldWarnWhenMonthHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHas4Digits();
        page.buyWithCard(card);
        page.verifyCardDate();
        String expectedYear = card.getYear().substring(0, 2);
        page.verifyFieldValueEquals("Год", expectedYear);
    }

    @Test
    @DisplayName("Validate month (input 3 digits of next year)")
    void shouldWarnWhenMonthHas3Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHas3Digits();
        String expectedYear = card.getYear().substring(0, 2);
        page.buyWithCard(card);
        page.verifyCardDate();
        page.verifyFieldValueEquals("Год", expectedYear);
    }

    @Test
    @DisplayName("Verify card date (input last month)")
    void shouldWarnWhenCardDateIsLastMonth() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastMonth();
        page.buyWithCard(card);
        page.verifyCardDate();
    }

    @Test
    @DisplayName("Verify card date (input last year)")
    void shouldWarnWhenCardDateIsLastYear() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastYear();
        page.buyWithCard(card);
        page.verifyCardDate();
    }

    @Test
    @DisplayName("Validate holder (empty)")
    void shouldWarnWhenHolderIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyHolder();
        page.buyWithCard(card);
        page.verifyHolderNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (one word in Latin)")
    void shouldWarnWhenHolderHasOneWordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderLastName();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (3 word in Latin)")
    void shouldWarnWhenHolderHas3WordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderFullName();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (1 word in Cyrillic)")
    void shouldWarnWhenHolderHas1WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderLastNameCyrillic();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (2 word in Cyrillic)")
    void shouldWarnWhenHolderHas2WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameCyrillic();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (3 word in Cyrillic)")
    void shouldWarnWhenHolderHas3WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderFullNameCyrillic();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has one symbol)")
    void shouldWarnWhenHolderHasOneSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHas1SymbolicWord();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has 2 symbolic words)")
    void shouldWarnWhenHolderHasTwoSymbolicWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHas2SymbolicWords();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has 2 numeric words)")
    void shouldWarnWhenHolderHasTwoNumericWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHasDigits();
        page.buyWithCard(card);
        page.verifyHolderInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (empty)")
    void shouldWarnWhenCodeIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCode();
        page.buyWithCard(card);
        page.verifyCodeNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (one digit)")
    void shouldWarnWhenCodeHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas1Digit();
        page.buyWithCard(card);
        page.verifyCodeInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (2 digits)")
    void shouldWarnWhenCodeHas2Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas2Digit();
        page.buyWithCard(card);
        page.verifyCodeInvalid();
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (4 digits)")
    void shouldWarnWhenCodeHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas4Digit();
        page.buyWithCard(card);
        page.verifyFieldValueEquals("CVC/CVV", card.getCode().substring(0, 3));
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate holder (input letters)")
    void shouldWarnWhenCodeHasLetters() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHasLetters();
        page.buyWithCard(card);
        page.verifyCodeNotFilled();
        page.verifyFieldValueEquals("CVC/CVV", "");
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (input symbols)")
    void shouldWarnWhenCodeHasSymbols() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHasSymbols();
        page.buyWithCard(card);
        page.verifyCodeNotFilled();
        page.verifyOtherFieldsAreValid();
    }

    private String getFormattedCardNumber(String number) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= number.length(); i++) {
            result.append(number.charAt(i));
            if (i % 4 == 0) {
                result.append(" ");
            }
        }
        return result.toString();
    }

}
