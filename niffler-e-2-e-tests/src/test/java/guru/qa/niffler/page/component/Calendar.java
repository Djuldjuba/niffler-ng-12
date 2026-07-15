package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class Calendar extends BaseComponent<Calendar> {

  private final SelenideElement input;
  private final SelenideElement calendarButton;
  private final SelenideElement prevMonthButton;
  private final SelenideElement nextMonthButton;
  private final SelenideElement currentMonthAndYear;
  private final ElementsCollection dateRows;

  public Calendar(SelenideDriver driver) {
    super(driver, driver.$(".MuiPickersLayout-root"));
    this.input = driver.$("input[name='date']");
    this.calendarButton = driver.$("button[aria-label*='Choose date']");
    this.prevMonthButton = self.$("button[title='Previous month']");
    this.nextMonthButton = self.$("button[title='Next month']");
    this.currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
    this.dateRows = self.$$(".MuiDayCalendar-weekContainer");
  }

  public Calendar() {
    super($(".MuiPickersLayout-root"));
    this.input = $("input[name='date']");
    this.calendarButton = $("button[aria-label*='Choose date']");
    this.prevMonthButton = self.$("button[title='Previous month']");
    this.nextMonthButton = self.$("button[title='Next month']");
    this.currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
    this.dateRows = self.$$(".MuiDayCalendar-weekContainer");
  }

  public Calendar(SelenideElement self) {
    super(self);
    this.input = $("input[name='date']");
    this.calendarButton = $("button[aria-label*='Choose date']");
    this.prevMonthButton = self.$("button[title='Previous month']");
    this.nextMonthButton = self.$("button[title='Next month']");
    this.currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
    this.dateRows = self.$$(".MuiDayCalendar-weekContainer");
  }
}