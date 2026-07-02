package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;

@ExtendWith({TestMethodContextExtension.class, UsersQueueExtension.class})
public class SpendingTest {

    @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
    void statComponentShouldDisplayArchivedCategories(
            BufferedImage expected,
            @UserType StaticUser user) throws IOException {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getStatComponent()
                .checkStatisticBubblesContains("Продукты 20000 ₽", "Здоровье 5000 ₽")
                .checkStatisticImage(expected)
                .checkBubbles(Color.yellow, Color.green);
    }

    @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = false)
    void statComponentColorWithText(
            BufferedImage expected,
            @UserType StaticUser user) throws IOException {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkBubblesWithText(
                        new Bubble(Color.yellow, "Продукты 20000 ₽"),
                        new Bubble(Color.green, "Здоровье 5000 ₽")
                );
    }

    @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = false)
    void statComponentShouldDisplayCategoriesInAnyOrder(
            BufferedImage expected,
            @UserType StaticUser user) throws IOException {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkBubblesWithTextInAnyOrder(
                        new Bubble(Color.green, "Здоровье 5000 ₽"),
                        new Bubble(Color.yellow, "Продукты 20000 ₽")
                );
    }

    @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
    void checkBubblesWithTextInAnyOrder(
            BufferedImage expected,
            @UserType StaticUser user) throws IOException {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getStatComponent()
                .checkStatisticBubblesContains("Продукты 20000 ₽", "Здоровье 5000 ₽")
                .checkStatisticImage(expected)
                .checkBubblesWithText(
                        new Bubble(Color.yellow, "Продукты 20000 ₽"),
                        new Bubble(Color.green, "Здоровье 5000 ₽")
                )
                .checkBubblesWithTextInAnyOrder(
                        new Bubble(Color.green, "Здоровье 5000 ₽"),
                        new Bubble(Color.yellow, "Продукты 20000 ₽")
                )
                .checkBubblesContains(
                        new Bubble(Color.yellow, "Продукты 20000 ₽")
                );
    }

    @Test
    void checkTable(@UserType StaticUser user) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

        CategoryJson categoryHealth = new CategoryJson(
                UUID.randomUUID(),
                "Здоровье",
                user.username(),
                false
        );

        CategoryJson categoryFood = new CategoryJson(
                UUID.randomUUID(),
                "Продукты",
                user.username(),
                false
        );

        SpendJson spend1 = new SpendJson(
                UUID.randomUUID(),
                dateFormat.parse("Jul 2, 2026"),
                categoryHealth,
                CurrencyValues.RUB,
                5000.0,
                "Аптека",
                user.username()
        );

        SpendJson spend2 = new SpendJson(
                UUID.randomUUID(),
                dateFormat.parse("May 3, 2026"),
                categoryFood,
                CurrencyValues.RUB,
                20000.0,
                "Еда",
                user.username()
        );

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getSpendingTable()
                .checkSpends(spend1, spend2);
    }

    @Test
    void checkTableWithNotEmptyUser(@UserType StaticUser user) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

        CategoryJson categoryHealth = new CategoryJson(
                UUID.randomUUID(),
                "Здоровье",
                user.username(),
                false
        );

        CategoryJson categoryFood = new CategoryJson(
                UUID.randomUUID(),
                "Продукты",
                user.username(),
                false
        );

        SpendJson spend1 = new SpendJson(
                UUID.randomUUID(),
                dateFormat.parse("Jul 2, 2026"),
                categoryHealth,
                CurrencyValues.RUB,
                5000.0,
                "Аптека",
                user.username()
        );

        SpendJson spend2 = new SpendJson(
                UUID.randomUUID(),
                dateFormat.parse("May 3, 2026"),
                categoryFood,
                CurrencyValues.RUB,
                20000.0,
                "Еда",
                user.username()
        );

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getSpendingTable()
                .checkSpends(spend1, spend2);
    }
}