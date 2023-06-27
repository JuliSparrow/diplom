package ru.netology.diplom;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ru.netology.diplom.utils.Card;

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

}
