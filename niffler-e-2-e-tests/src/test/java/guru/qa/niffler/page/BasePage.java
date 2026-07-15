package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

  protected final SelenideDriver driver;
  protected final SelenideElement alert;
  protected final ElementsCollection formErrors;

  protected BasePage(SelenideDriver driver) {
    this.driver = driver;
    this.alert = driver.$(".MuiSnackbar-root");
    this.formErrors = driver.$$("p.Mui-error, .input__helper-text");
  }

  protected BasePage() {
    this.driver = null;
    this.alert = $(".MuiSnackbar-root");
    this.formErrors = $$("p.Mui-error, .input__helper-text");
  }

  @SuppressWarnings("unchecked")
  public T checkAlert(String text) {
    alert.shouldHave(text(text));
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T checkFormErrorMessage(String... expectedText) {
    formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
    return (T) this;
  }
}