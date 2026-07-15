package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class SelectField extends BaseComponent<SelectField> {

  private final SelenideElement input;

  public SelectField(SelenideDriver driver, SelenideElement self) {
    super(driver, self);
    this.input = self.$("input");
  }

  public SelectField(SelenideElement self) {
    super(self);
    this.input = self.$("input");
  }

  public void setValue(String value) {
    self.click();
    if (driver != null) {
      driver.$$("li[role='option']").find(text(value)).click();
    } else {
      $$("li[role='option']").find(text(value)).click();
    }
  }

  public void checkSelectValueIsEqualTo(String value) {
    self.shouldHave(text(value));
  }
}