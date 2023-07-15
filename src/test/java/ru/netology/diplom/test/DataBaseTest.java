package ru.netology.diplom.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.page.ShopPage;
import ru.netology.diplom.utils.Card;
import ru.netology.diplom.utils.CardUtil;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.diplom.utils.DBUtils.*;

public class DataBaseTest {
    private static final String INVALID_FORMAT_MESSAGE = "Неверный формат";

    @BeforeAll
    public static void setupAll() {
        initConnection();
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        closeConnection();
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should add record to database when successful pay with card")
    void shouldAddRecordToDatabaseWhenSuccessfulPayWithCard() {
        var page = Selenide.open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount() + 1;
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getFirstCard();
        page.buyWithCard(card);
        page.verifySuccessNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "APPROVED";
        String actualStatus = getPaymentEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Should add record to database when pay with card rejected")
    void shouldAddRecordToDatabaseWhenPayWithCardRejected() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount() + 1;
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getSecondCard();
        page.buyWithCard(card);
        page.verifyErrorNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "DECLINED";
        String actualStatus = getPaymentEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Should add record to database when successful pay with credit")
    void shouldAddRecordToDatabaseWhenSuccessfulPayWithCredit() {
        var page = Selenide.open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount() + 1;
        Card card = CardUtil.getFirstCard();
        page.buyWithCredit(card);
        page.verifySuccessNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "APPROVED";
        String actualStatus = getCreditRequestEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Should add record to database when pay with credit rejected")
    void shouldAddRecordToDatabaseWhenPayWithCreditRejected() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount() + 1;
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount() + 1;
        Card card = CardUtil.getSecondCard();
        page.buyWithCredit(card);
        page.verifyErrorNotification();
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
        String expectedStatus = "DECLINED";
        String actualStatus = getCreditRequestEntityStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Should not add records to database when form is invalid")
    void shouldNotAddRecordsToDatabaseWhenFormIsInvalid() {
        var page = open("http://localhost:8080", ShopPage.class);
        int orderEntityCountExpected = getOrderEntityCount();
        int paymentEntityCountExpected = getPaymentEntityCount();
        int creditRequestEntityCountExpected = getCreditRequestEntityCount();
        Card card = CardUtil.getCardWithNumberLessThen16Digits();
        page.buyWithCard(card);
        page.verifyCardNumberInvalid(INVALID_FORMAT_MESSAGE);
        verifyDataBase(orderEntityCountExpected, paymentEntityCountExpected, creditRequestEntityCountExpected);
    }

    public static void verifyDataBase(int orderEntityCountExpected, int paymentEntityCountExpected, int creditRequestEntityCountExpected) {
        int orderEntityCountActual = getOrderEntityCount();
        int paymentEntityCountActual = getPaymentEntityCount();
        int creditRequestEntityActual = getCreditRequestEntityCount();
        Assertions.assertEquals(orderEntityCountExpected, orderEntityCountActual);
        Assertions.assertEquals(paymentEntityCountExpected, paymentEntityCountActual);
        Assertions.assertEquals(creditRequestEntityCountExpected, creditRequestEntityActual);
    }
}
