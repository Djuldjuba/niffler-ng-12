package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class RegisterPage extends BasePage<RegisterPage> {

  public static final String URL = CFG.authUrl() + "register";

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement passwordSubmitInput;
  private final SelenideElement submitButton;
  private final SelenideElement proceedLoginButton;
  private final SelenideElement errorContainer;

  public RegisterPage(SelenideDriver driver) {
    super(driver);
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.passwordSubmitInput = driver.$("input[name='passwordSubmit']");
    this.submitButton = driver.$("button[type='submit']");
    this.proceedLoginButton = driver.$(".form_sign-in");
    this.errorContainer = driver.$(".form__error");
  }

  public RegisterPage() {
    super();
    this.usernameInput = com.codeborne.selenide.Selenide.$("input[name='username']");
    this.passwordInput = com.codeborne.selenide.Selenide.$("input[name='password']");
    this.passwordSubmitInput = com.codeborne.selenide.Selenide.$("input[name='passwordSubmit']");
    this.submitButton = com.codeborne.selenide.Selenide.$("button[type='submit']");
    this.proceedLoginButton = com.codeborne.selenide.Selenide.$(".form_sign-in");
    this.errorContainer = com.codeborne.selenide.Selenide.$(".form__error");
  }

  public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
    setUsername(login);
    setPassword(password);
    setPasswordSubmit(passwordSubmit);
    return this;
  }

  public RegisterPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public RegisterPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public RegisterPage setPasswordSubmit(String password) {
    passwordSubmitInput.setValue(password);
    return this;
  }

  public LoginPage successSubmit() {
    submitButton.click();
    proceedLoginButton.click();
    return driver != null ? new LoginPage(driver) : new LoginPage();
  }

  public RegisterPage errorSubmit() {
    submitButton.click();
    return this;
  }

  public RegisterPage checkThatPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    passwordSubmitInput.should(visible);
    return this;
  }

  public RegisterPage checkAlertMessage(String errorMessage) {
    errorContainer.shouldHave(text(errorMessage));
    return this;
  }
}