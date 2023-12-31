package ru.netology.diplom.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ru.netology.diplom.utils.Card;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
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
        $$("fieldset .input.input_invalid").shouldBe(empty, Duration.ofSeconds(5));
    }

    public void verifySuccessNotification() {
        $(".notification.notification_status_ok").shouldBe(visible, Duration.ofSeconds(15));
    }

    public void verifyErrorNotification() {
        $(".notification.notification_status_error").shouldBe(visible, Duration.ofSeconds(15));
    }

    public void verifyCardNumberInvalid(String expectedMessage) {
        $$(".input .input__inner").findBy(text("Номер карты")).find(".input__sub").shouldHave(text(expectedMessage), Duration.ofSeconds(15));
    }

    public void verifyMonthInvalid(String expectedMessage) {
        $$(".input .input__inner").findBy(text("Месяц")).find(".input__sub").shouldHave(text(expectedMessage), Duration.ofSeconds(15));
    }

    public void verifyYearInvalid(String expectedMessage) {
        $$(".input .input__inner").findBy(text("Год")).find(".input__sub").shouldHave(text(expectedMessage), Duration.ofSeconds(15));
    }

    public void verifyHolderInvalid(String expectedMessage) {
        $$(".input .input__inner").findBy(text("Владелец")).find(".input__sub").shouldHave(text(expectedMessage), Duration.ofSeconds(15));
    }

    public void verifyCodeInvalid(String expectedMessage) {
        $$(".input .input__inner").findBy(text("CVC/CVV")).find(".input__sub").shouldHave(text(expectedMessage), Duration.ofSeconds(15));
    }

    public void verifyOtherFieldsAreValid() {
        $$("fieldset .input.input_invalid").shouldHave(size(1), Duration.ofSeconds(15));
    }
}
