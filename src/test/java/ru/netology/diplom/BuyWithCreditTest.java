package ru.netology.diplom;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.utils.Card;
import ru.netology.diplom.utils.CardUtil;
import ru.netology.diplom.utils.DateUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.util.Properties;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BuyWithCreditTest {
    private static Connection connection;
    private static final String MUST_BE_FILLED_MESSAGE = "Поле обязательно для заполнения";
    private static final String INVALID_FORMAT_MESSAGE = "Неверный формат";
    private static final String CARD_DATE_EXPIRED_MESSAGE = "Истёк срок действия карты";
    private static final String WRONG_CARD_DATE_MESSAGE = "Неверно указан срок действия карты";
    
    @BeforeAll
    public static void setupAll() {
        Properties properties = new Properties();
        String fileName = "artifacts/application.properties";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = properties.getProperty("spring.datasource.url");
        String user = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Successful Pay With Credit")
    void shouldSuccessfulPayWithCredit() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount() + 1;
        Card card = CardUtil.getFirstCard();
        page.buyWithCredit(card);
        verifyNoInvalidFields();
        verifySuccessNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "APPROVED";
        String actualStatus = getPaymentEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Reject Pay With Credit")
    void shouldRejectPayWithCredit() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount() + 1;
        Card card = CardUtil.getSecondCard();
        page.buyWithCredit(card);
        verifyNoInvalidFields();
        verifyErrorNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "DECLINED";
        String actualStatus = getPaymentEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Validate card number (empty)")
    void shouldWarnWhenCardNumberEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithNumber("");
        page.buyWithCredit(card);
        verifyCardNumberNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate card number (input more then 16 digits)")
    void shouldWarnWhenCardNumberHasMoreThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCard();
        card.setNumber("11112222333344445");
        page.buyWithCredit(card);
        verifyFieldValueEquals("Номер карты", "1111 2222 3333 4444");
        verifyErrorNotification();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate card number (input less then 16 digits)")
    void shouldWarnWhenCardNumberHasLessThen16Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithNumber("444444444444444");
        page.buyWithCredit(card);
        verifyCardNumberInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate card number (number has letter)")
    void shouldWarnWhenCardNumberHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithNumber("444444444444444i");
        page.buyWithCredit(card);
        verifyCardNumberInvalid();
        verifyFieldValueEquals("Номер карты", "4444 4444 4444 444");
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate card number (number has symbol)")
    void shouldWarnWhenCardNumberHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithNumber("444444444444444i");
        page.buyWithCredit(card);
        verifyCardNumberInvalid();
        verifyFieldValueEquals("Номер карты", "4444 4444 4444 444");
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (empty)")
    void shouldWarnWhenMonthIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("");
        page.buyWithCredit(card);
        verifyMonthNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input 0)")
    void shouldWarnWhenMonthIsZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("0");
        page.buyWithCredit(card);
        verifyMonthInvalidFormat();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input 00)")
    void shouldWarnWhenMonthIsDoubleZero() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("00");
        page.buyWithCredit(card);
        verifyMonthInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input 7)")
    void shouldWarnWhenMonthHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithNextYear();
        card.setMonth("7");
        page.buyWithCredit(card);
        verifyMonthInvalidFormat();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input 007)")
    void shouldWarnWhenMonthHas3Digit() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("007");
        page.buyWithCredit(card);
        verifyFieldValueEquals("Месяц", "00");
        verifyMonthInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input 13)")
    void shouldWarnWhenMonthNotExists() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("13");
        page.buyWithCredit(card);
        verifyMonthInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (input negative value)")
    void shouldWarnWhenMonthIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount() + 1;
        Card card = CardUtil.getFirstCard();
        String expectedMonth = card.getMonth();
        String negativeMonth = "-" + card.getMonth();
        card.setMonth(negativeMonth);
        page.buyWithCredit(card);
        verifySuccessNotification();
        verifyFieldValueEquals("Месяц", expectedMonth);
        verifyNoInvalidFields();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (has letter)")
    void shouldWarnWhenMonthHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("VI");
        page.buyWithCredit(card);
        verifyMonthNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate month (has symbol)")
    void shouldWarnWhenMonthHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithMonth("0%");
        page.buyWithCredit(card);
        verifyFieldValueEquals("Месяц", "0");
        verifyMonthInvalidFormat();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate year (empty)")
    void shouldWarnWhenYearIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithYear("");
        page.buyWithCredit(card);
        verifyYearNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate year (has letter)")
    void shouldWarnWhenYearHasLetter() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithYear("XX");
        page.buyWithCredit(card);
        verifyYearNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate year (has symbol)")
    void shouldWarnWhenYearHasSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithYear("2*");
        page.buyWithCredit(card);
        verifyFieldValueEquals("Год", "2");
        verifyYearInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate year (input negative value)")
    void shouldWarnWhenYearIsNegative() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCard();
        String cardYear = card.getYear();
        String negativeYear = "-" + cardYear;
        card.setYear(negativeYear);
        page.buyWithCredit(card);
        verifySuccessNotification();
        verifyNoInvalidFields();
        verifyFieldValueEquals("Год", cardYear);
    }

    @Test
    @DisplayName("Validate month (input 4 digits of next year)")
    void shouldWarnWhenMonthHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        String nextYear = DateUtils.getNextYearAs4Digits();
        Card card = CardUtil.getFirstCardWithYear(nextYear);
        page.buyWithCredit(card);
        verifyCardDate();
        String expectedYear = nextYear.substring(0, 2);
        verifyFieldValueEquals("Год", expectedYear);
    }

    @Test
    @DisplayName("Validate month (input 3 digits of next year)")
    void shouldWarnWhenMonthHas3Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        String nextYear = DateUtils.getNextYearAs3Digits();
        String expectedYear = nextYear.substring(0, 2);
        Card card = CardUtil.getFirstCardWithYear(nextYear);
        page.buyWithCredit(card);
        verifyCardDate();
        verifyFieldValueEquals("Год", expectedYear);
    }

    @Test
    @DisplayName("Verify card date (input last month)")
    void shouldWarnWhenCardDateIsLastMonth() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastMonth();
        page.buyWithCredit(card);
        verifyCardDate();
    }

    @Test
    @DisplayName("Verify card date (input last year)")
    void shouldWarnWhenCardDateIsLastYear() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithLastYear();
        page.buyWithCredit(card);
        verifyCardDate();
    }

    @Test
    @DisplayName("Validate holder (empty)")
    void shouldWarnWhenHolderIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("");
        page.buyWithCredit(card);
        verifyHolderNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (one word in Latin)")
    void shouldWarnWhenHolderHasOneWordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("PETROV");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (3 word in Latin)")
    void shouldWarnWhenHolderHas3WordInLatin() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("PETROV SIDOR IVANOVICH");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (1 word in Cyrillic)")
    void shouldWarnWhenHolderHas1WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("Петров");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (2 word in Cyrillic)")
    void shouldWarnWhenHolderHas2WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("Петров Сидор");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (3 word in Cyrillic)")
    void shouldWarnWhenHolderHas3WordInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("Петров Сидор Иванович");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (has double lastname and name in Cyrillic)")
    void shouldWarnWhenHolderHasDoubleLastnameAndNameInCyrillic() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("Петров-Старший Сидор");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (has one symbol)")
    void shouldWarnWhenHolderHasOneSymbol() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("*");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (has 2 symbolic words)")
    void shouldWarnWhenHolderHasTwoSymbolicWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("* *");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (has 2 numeric words)")
    void shouldWarnWhenHolderHasTwoNumericWords() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithHolder("123 456");
        page.buyWithCredit(card);
        verifyHolderInvalid();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate code (empty)")
    void shouldWarnWhenCodeIsEmpty() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCardWithCode("");
        page.buyWithCredit(card);
        verifyCodeNotFilled();
        verifyOtherFieldsAreValid();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    @Test
    @DisplayName("Validate holder (one digit)")
    void shouldWarnWhenCodeHasOneDigit() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithCode("9");
        page.buyWithCredit(card);
        verifyCodeInvalid();
        verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (2 digits)")
    void shouldWarnWhenCodeHas2Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithCode("99");
        page.buyWithCredit(card);
        verifyCodeInvalid();
        verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (4 digits)")
    void shouldWarnWhenCodeHas4Digits() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithCode("9999");
        page.buyWithCredit(card);
        verifyFieldValueEquals("CVC/CVV", "999");
        verifyNoInvalidFields();
    }

    @Test
    @DisplayName("Validate holder (input letters)")
    void shouldWarnWhenCodeHasLetters() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithCode("abc");
        page.buyWithCredit(card);
        verifyCodeNotFilled();
        verifyFieldValueEquals("CVC/CVV", "");
        verifyOtherFieldsAreValid();
    }

    @Test
    @DisplayName("Validate holder (input symbols)")
    void shouldWarnWhenCodeHasSymbols() {
        var page = open("http://localhost:8080", ShopPage.class);
        Card card = CardUtil.getFirstCardWithCode("!@#");
        page.buyWithCredit(card);
        verifyCodeNotFilled();
        verifyOtherFieldsAreValid();
    }

    private int getOrderEntityCount() {
        return selectCountFromTable("order_entity");
    }

    private int getPaymentEntityCount() {
        return selectCountFromTable("payment_entity");
    }

    private int getCreditRequestEntityCount() {
        return selectCountFromTable("credit_request_entity");
    }

    private int selectCountFromTable(String tableName) {
        String query = "SELECT count(*) as count FROM " + tableName;
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPaymentEntityStatus() {
        String query = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getString("status");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void verifyDataBase(int orderEntityCountExpected, int paymentEntityCountExpected, int creditRequestEntityCountExpected) {
        int orderEntityCountActual = getOrderEntityCount();
        int paymentEntityCountActual = getPaymentEntityCount();
        int creditRequestEntityActual = getCreditRequestEntityCount();
        Assertions.assertEquals(orderEntityCountExpected, orderEntityCountActual);
        Assertions.assertEquals(paymentEntityCountExpected, paymentEntityCountActual);
        Assertions.assertEquals(creditRequestEntityCountExpected, creditRequestEntityActual);
    }

    public void verifyNoInvalidFields() {
        Assertions.assertTrue($$("fieldset .input.input_invalid").isEmpty());
    }

    public void verifySuccessNotification() {
        $(".notification.notification_status_ok").shouldBe(visible, Duration.ofSeconds(15));
    }

    public void verifyErrorNotification() {
        $(".notification.notification_status_error").shouldBe(visible, Duration.ofSeconds(15));
    }

    public void verifyCardNumberNotFilled() {
        $$(".input .input__inner").findBy(text("Номер карты")).find(".input__sub").shouldHave(text(MUST_BE_FILLED_MESSAGE));
    }

    public void verifyCardNumberInvalid() {
        $$(".input .input__inner").findBy(text("Номер карты")).find(".input__sub").shouldHave(text(INVALID_FORMAT_MESSAGE));
    }

    public void verifyMonthNotFilled() {
        $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(MUST_BE_FILLED_MESSAGE));
    }

    public void verifyMonthInvalidFormat() {
        $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(INVALID_FORMAT_MESSAGE));
    }

    public void verifyMonthInvalid() {
        $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(WRONG_CARD_DATE_MESSAGE));
    }

    public void verifyYearNotFilled() {
        $$(".input .input__inner").findBy(text("Год")).find(".input__sub").shouldHave(text(MUST_BE_FILLED_MESSAGE));
    }

    public void verifyYearInvalid() {
        $$(".input .input__inner").findBy(text("Год")).find(".input__sub").shouldHave(text(INVALID_FORMAT_MESSAGE));
    }

    public void verifyCardDate() {
        String monthValue = $$(".input .input__inner").findBy(text("Месяц")).find("input.input__control").getValue();
        String yearValue = $$(".input .input__inner").findBy(text("Год")).find("input.input__control").getValue();
        assert monthValue != null;
        assert yearValue != null;
        int month = Integer.parseInt(monthValue);
        int year = Integer.parseInt(yearValue);
        int currentYear = DateUtils.getCurrentYear();
        int currentMonth = DateUtils.getCurrentMonth();
        if (year < currentYear) {
            $$(".input .input__inner").findBy(text("Год")).find(".input__sub").shouldHave(text(CARD_DATE_EXPIRED_MESSAGE));
            $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(CARD_DATE_EXPIRED_MESSAGE));
        }
        if (year == currentYear && month < currentMonth) {
            $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(CARD_DATE_EXPIRED_MESSAGE));
        }
    }

    public void verifyHolderNotFilled() {
        $$(".input .input__inner").findBy(text("Владелец")).find(".input__sub").shouldHave(text(MUST_BE_FILLED_MESSAGE));
    }

    public void verifyHolderInvalid() {
        $$(".input .input__inner").findBy(text("Владелец")).find(".input__sub").shouldHave(text(INVALID_FORMAT_MESSAGE));
    }

    public void verifyCodeNotFilled() {
        $$(".input .input__inner").findBy(text("CVC/CVV")).find(".input__sub").shouldHave(text(MUST_BE_FILLED_MESSAGE));
    }

    public void verifyCodeInvalid() {
        $$(".input .input__inner").findBy(text("CVC/CVV")).find(".input__sub").shouldHave(text(INVALID_FORMAT_MESSAGE));
    }

    private void verifyFieldValueEquals(String fieldName, String expected) {
        String actual = $$(".input .input__inner").findBy(text(fieldName)).find(".input__box .input__control").getAttribute("value");
        Assertions.assertEquals(expected, actual);
    }

    public void verifyOtherFieldsAreValid() {
        ElementsCollection invalidElements = $$("fieldset .input.input_invalid");
        Assertions.assertEquals(1, invalidElements.size());
    }

}
