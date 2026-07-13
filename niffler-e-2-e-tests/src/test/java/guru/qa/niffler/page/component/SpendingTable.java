package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendingConditions.spends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

  private final SearchField searchField = new SearchField();
  private final SelenideElement periodMenu = self.$("#period");
  private final SelenideElement currencyMenu = self.$("#currency");
  private final ElementsCollection menuItems = $$(".MuiList-padding li");
  private final SelenideElement deleteBtn = self.$("#delete");
  private final SelenideElement popup = $("div[role='dialog']");

  private final SelenideElement tableHeader = self.$(".MuiTableHead-root");
  private final ElementsCollection headerCells = tableHeader.$$(".MuiTableCell-root");

  private final ElementsCollection tableRows = self.$("tbody").$$("tr");


  public SpendingTable() {
    super($("#spendings"));
  }

  public SpendingTable checkSpends(SpendJson... expectedSpends) {
    tableRows.should(spends(expectedSpends));
    return this;
  }
}
