package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Selenide.$;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Calendar extends BaseComponent<Calendar> {

  public Calendar(SelenideElement self) {
    super(self);
  }

  public Calendar() {
    super($(".MuiPickersLayout-root"));
  }

  private final SelenideElement input = $("input[name='date']");
  private final SelenideElement calendarButton = $("button[aria-label*='Choose date']");
  private final SelenideElement prevMonthButton = self.$("button[title='Previous month']");
  private final SelenideElement nextMonthButton = self.$("button[title='Next month']");
  private final SelenideElement currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
  private final ElementsCollection dateRows = self.$$(".MuiDayCalendar-weekContainer");
}
