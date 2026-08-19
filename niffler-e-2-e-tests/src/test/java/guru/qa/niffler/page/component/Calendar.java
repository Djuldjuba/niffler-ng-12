package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;

public class Calendar extends BaseComponent<Calendar> {

  public Calendar(SelenideElement self) {
    super(self);
  }

  public Calendar() {
    super($(".MuiPickersLayout-root"));
  }
}
