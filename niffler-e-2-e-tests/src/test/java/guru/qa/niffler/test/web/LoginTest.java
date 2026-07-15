package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.BrowserConverter;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class LoginTest {

  private SelenideDriver chrome;
  private SelenideDriver firefox;

  @BeforeEach
  void setUp() {
    chrome = new SelenideDriver(SelenideUtils.chromeConfig);
    firefox = new SelenideDriver(SelenideUtils.firefoxConfig);
    BrowserExtension.registerDriver(chrome);
    BrowserExtension.registerDriver(firefox);
  }

  @Test
  void twoBrowsersTest() {
    chrome.open(LoginPage.URL);
    new LoginPage(chrome)
            .fillLoginPage(randomUsername(), "BAD")
            .submit(new LoginPage(chrome))
            .checkError("Неверные учетные данные пользователя");

    firefox.open(LoginPage.URL);
    new LoginPage(firefox)
            .fillLoginPage(randomUsername(), "BAD")
            .submit(new LoginPage(firefox))
            .checkError("Неверные учетные данные пользователя");
  }

  @ParameterizedTest()
  @ValueSource(strings = {"chrome", "firefox"})
  void twoBrowsersParameterizedTest(
          @ConvertWith(BrowserConverter.class) String browserName) {

    SelenideDriver driver = browserName.equals("chrome")
            ? new SelenideDriver(SelenideUtils.chromeConfig)
            : new SelenideDriver(SelenideUtils.firefoxConfig);

    BrowserExtension.registerDriver(driver);

    driver.open(LoginPage.URL);
    new LoginPage(driver)
            .fillLoginPage(randomUsername(), "BAD")
            .submit(new LoginPage(driver))
            .checkError("Неверные учетные данные пользователя");
  }
}