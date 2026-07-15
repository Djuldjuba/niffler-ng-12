package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

  private final SelenideElement clearSearchInputBtn;

  public SearchField(SelenideDriver driver) {
    super(driver, driver.$("input[aria-label='search']"));
    this.clearSearchInputBtn = driver.$("#input-clear");
  }

  public SearchField() {
    super($("input[aria-label='search']"));
    this.clearSearchInputBtn = $("#input-clear");
  }

  public SearchField(SelenideElement self) {
    super(self);
    this.clearSearchInputBtn = $("#input-clear");
  }

  public SearchField search(String query) {
    clearIfNotEmpty();
    self.setValue(query).pressEnter();
    return this;
  }

  public SearchField clearIfNotEmpty() {
    if (self.is(not(empty))) {
      clearSearchInputBtn.click();
      self.should(empty);
    }
    return this;
  }
}