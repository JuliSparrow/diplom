package ru.netology.diplom.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ru.netology.diplom.utils.Card;
import ru.netology.diplom.utils.DateUtils;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class ShopPage {
    private final SelenideElement buyWithCardButton = $$("button .button__text").find(text("Купить"));
    private final SelenideElement buyWithCreditButton = $$("button .button__text").find(text("Купить в кредит"));
    private final SelenideElement cardNumberField = $$(".input .input__inner").findBy(text("Номер карты")).$("input.input__control");
    private final SelenideElement monthField = $$(".input .input__inner").findBy(text("Месяц")).$("input.input__control");
    private final SelenideElement yearField = $$(".input .input__inner").findBy(text("Год")).$("input.input__control");
    private final SelenideElement holderField = $$(".input .input__inner").findBy(text("Владелец")).$("input.input__control");
    private final SelenideElement codeField = $$(".input .input__inner").findBy(text("CVC/CVV")).$("input.input__control");
    private final SelenideElement continueButton = $$("button .button__text").find(text("Продолжить"));
    private static final String MUST_BE_FILLED_MESSAGE = "Поле обязательно для заполнения";
    private static final String INVALID_FORMAT_MESSAGE = "Неверный формат";
    private static final String CARD_DATE_EXPIRED_MESSAGE = "Истёк срок действия карты";
    private static final String WRONG_CARD_DATE_MESSAGE = "Неверно указан срок действия карты";

    public ShopPage() {
        $("h2.heading").shouldHave(text("Путешествие дня"));
    }

    public ShopPage buyWithCard(Card card) {
        buyWithCardButton.click();
        $$("h3").find(text("Оплата по карте")).shouldBe(visible);
        checkFormElements();
        fillForm(card);
        continueButton.click();
        return this;
    }

    public ShopPage buyWithCredit(Card card) {
        buyWithCreditButton.click();
        $$("h3").find(text("Кредит по данным карты")).shouldBe(visible);
        checkFormElements();
        fillForm(card);
        continueButton.click();
        return this;
    }

    private void checkFormElements() {
        cardNumberField.shouldBe(visible);
        monthField.shouldBe(visible);
        yearField.shouldBe(visible);
        holderField.shouldBe(visible);
        codeField.shouldBe(visible);
        continueButton.shouldBe(visible);
    }

    private void fillForm(Card card) {
        cardNumberField.setValue(card.getNumber());
        monthField.setValue(card.getMonth());
        yearField.setValue(card.getYear());
        holderField.setValue(card.getHolder());
        codeField.setValue(card.getCode());
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

    public void verifyFieldValueEquals(String fieldName, String expected) {
        String actual = $$(".input .input__inner").findBy(text(fieldName)).find(".input__box .input__control").getAttribute("value");
        Assertions.assertEquals(expected, actual);
    }

    public void verifyOtherFieldsAreValid() {
        ElementsCollection invalidElements = $$("fieldset .input.input_invalid");
        Assertions.assertEquals(1, invalidElements.size());
    }

}
