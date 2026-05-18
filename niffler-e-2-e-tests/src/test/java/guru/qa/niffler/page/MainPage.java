package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage {

  private final ElementsCollection tableRows = $("table tbody").$$("tr");

  public EditSpendingPage openSpendingByDescription(String description) {
    tableRows.find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContainsSpending(String description) {
    tableRows.find(text(description)).should(visible);
    return this;
  }
}
