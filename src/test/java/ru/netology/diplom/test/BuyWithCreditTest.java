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

public class BuyWithCreditTest {
    private static final String MUST_BE_FILLED_MESSAGE = "Поле обязательно для заполнения";
    private static final String INVALID_FORMAT_MESSAGE = "Неверный формат";
    private static final String CARD_DATE_EXPIRED_MESSAGE = "Истёк срок действия карты";
    private static final String WRONG_CARD_DATE_MESSAGE = "Неверно указан срок действия карты";
    
    @BeforeAll
    public static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Successful Pay With Credit")
    void shouldSuccessfulPayWithCredit() {
        var page = Selenide.open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCard();
        page.buyWithCredit(card);
        page.verifyNoInvalidFields();
        page.verifySuccessNotification();
    }

    @Test
    @DisplayName("Reject Pay With Credit")
    void shouldRejectPayWithCredit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getSecondCard();
        page.buyWithCredit(card);
        page.verifyNoInvalidFields();
        page.verifyErrorNotification();
    }

    @Test
    @DisplayName("Validate card number (empty)")
    void shouldWarnWhenCardNumberEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithEmptyNumber();
        page.buyWithCredit(card);
        page.verifyCardNumberInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (input more then 16 digits)")
    void shouldWarnWhenCardNumberHasMoreThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberMoreThen16Digits();
        page.buyWithCredit(card);
        page.verifyErrorNotification();
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate card number (input less then 16 digits)")
    void shouldWarnWhenCardNumberHasLessThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberLessThen16Digits();
        page.buyWithCredit(card);
        page.verifyCardNumberInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (number has letter)")
    void shouldWarnWhenCardNumberHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberHasLetter();
        page.buyWithCredit(card);
        page.verifyCardNumberInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate card number (number has symbol)")
    void shouldWarnWhenCardNumberHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getCardWithNumberHasSymbol();
        page.buyWithCredit(card);
        page.verifyCardNumberInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (empty)")
    void shouldWarnWhenMonthIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyMonth();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 0)")
    void shouldWarnWhenMonthIsZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthZero();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 00)")
    void shouldWarnWhenMonthIsDoubleZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthDoubleZero();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input one digit)")
    void shouldWarnWhenMonthHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHas1Digit();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input three digits)")
    void shouldWarnWhenMonthHas3Digit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHas3Digits();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input 13)")
    void shouldWarnWhenMonthNotExists() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthMoreThen12();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(WRONG_CARD_DATE_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (input negative value)")
    void shouldWarnWhenMonthIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithNegativeMonth();
        page.buyWithCredit(card);
        page.verifySuccessNotification();
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate month (has letter)")
    void shouldWarnWhenMonthHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHasLetters();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate month (has symbol)")
    void shouldWarnWhenMonthHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithMonthHasSymbols();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (empty)")
    void shouldWarnWhenYearIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyYear();
        page.buyWithCredit(card);
        page.verifyYearInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (has letter)")
    void shouldWarnWhenYearHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHasLetter();
        page.buyWithCredit(card);
        page.verifyYearInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (has symbol)")
    void shouldWarnWhenYearHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHasSymbol();
        page.buyWithCredit(card);
        page.verifyYearInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate year (input negative value)")
    void shouldWarnWhenYearIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithNegativeYear();
        page.buyWithCredit(card);
        page.verifySuccessNotification();
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate year (input 4 digits of next year)")
    void shouldWarnWhenYearHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHas4Digits();
        page.buyWithCredit(card);
        page.verifyYearInvalid(CARD_DATE_EXPIRED_MESSAGE);
    }

    @Test
    @DisplayName("Validate year (input 3 digits of next year)")
    void shouldWarnWhenYearHas3Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHas3Digits();
        page.buyWithCredit(card);
        page.verifyYearInvalid(CARD_DATE_EXPIRED_MESSAGE);
    }

    @Test
    @DisplayName("Validate year (input 1 digit of next year)")
    void shouldWarnWhenYearHas1Digit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithYearHas1Digit();
        page.buyWithCredit(card);
        page.verifyYearInvalid(INVALID_FORMAT_MESSAGE);
    }

    @Test
    @DisplayName("Month should expired when input last month")
    void shouldWarnWhenCardDateIsLastMonth() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastMonth();
        page.buyWithCredit(card);
        page.verifyMonthInvalid(CARD_DATE_EXPIRED_MESSAGE);
    }

    @Test
    @DisplayName("Year should expired when input last year")
    void shouldWarnWhenCardDateIsLastYear() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastYear();
        page.buyWithCredit(card);
        page.verifyYearInvalid(CARD_DATE_EXPIRED_MESSAGE);
    }

    @Test
    @DisplayName("Validate holder (empty)")
    void shouldWarnWhenHolderIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyHolder();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (one word in Latin)")
    void shouldWarnWhenHolderHasOneWordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderLastName();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (3 word in Latin)")
    void shouldWarnWhenHolderHas3WordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderFullName();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (1 word in Cyrillic)")
    void shouldWarnWhenHolderHas1WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderLastNameCyrillic();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (2 word in Cyrillic)")
    void shouldWarnWhenHolderHas2WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameCyrillic();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (3 word in Cyrillic)")
    void shouldWarnWhenHolderHas3WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderFullNameCyrillic();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has one symbol)")
    void shouldWarnWhenHolderHasOneSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHas1SymbolicWord();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has 2 symbolic words)")
    void shouldWarnWhenHolderHasTwoSymbolicWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHas2SymbolicWords();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (has 2 numeric words)")
    void shouldWarnWhenHolderHasTwoNumericWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithHolderNameHasDigits();
        page.buyWithCredit(card);
        page.verifyHolderInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (empty)")
    void shouldWarnWhenCodeIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCode();
        page.buyWithCredit(card);
        page.verifyCodeInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (one digit)")
    void shouldWarnWhenCodeHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas1Digit();
        page.buyWithCredit(card);
        page.verifyCodeInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (2 digits)")
    void shouldWarnWhenCodeHas2Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas2Digit();
        page.buyWithCredit(card);
        page.verifyCodeInvalid(INVALID_FORMAT_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (4 digits)")
    void shouldWarnWhenCodeHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHas4Digit();
        page.buyWithCredit(card);
        page.verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate code (input letters)")
    void shouldWarnWhenCodeHasLetters() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHasLetters();
        page.buyWithCredit(card);
        page.verifyCodeInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate code (input symbols)")
    void shouldWarnWhenCodeHasSymbols() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithEmptyCodeHasSymbols();
        page.buyWithCredit(card);
        page.verifyCodeInvalid(MUST_BE_FILLED_MESSAGE);
        page.verifyOtherFieldsAreValid();
    }
}
