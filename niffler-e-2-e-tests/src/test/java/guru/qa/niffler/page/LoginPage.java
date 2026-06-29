package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

  public static final String URL = CFG.authUrl() + "login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement registerButton = $("a[href='/register']");
  private final SelenideElement errorContainer = $(".form__error");

  public RegisterPage doRegister() {
    registerButton.click();
    return new RegisterPage();
  }

  public LoginPage fillLoginPage(String login, String password) {
    setUsername(login);
    setPassword(password);
    return this;
  }

  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public <T extends BasePage<?>> T submit(T expectedPage) {
    submitButton.click();
    return expectedPage;
  }

  public LoginPage checkError(String error) {
    errorContainer.shouldHave(text(error));
    return this;
  }

  public LoginPage checkThatPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    return this;
  }
}
