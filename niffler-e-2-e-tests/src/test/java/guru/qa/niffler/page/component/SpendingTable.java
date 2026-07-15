package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendingConditions.spends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

  private final SearchField searchField;
  private final SelenideElement periodMenu;
  private final SelenideElement currencyMenu;
  private final ElementsCollection menuItems;
  private final SelenideElement deleteBtn;
  private final SelenideElement popup;
  private final SelenideElement tableHeader;
  private final ElementsCollection headerCells;
  private final ElementsCollection tableRows;

  public SpendingTable(SelenideDriver driver) {
    super(driver, driver.$("#spendings"));
    this.searchField = new SearchField(driver);
    this.periodMenu = self.$("#period");
    this.currencyMenu = self.$("#currency");
    this.menuItems = driver.$$(".MuiList-padding li");
    this.deleteBtn = self.$("#delete");
    this.popup = driver.$("div[role='dialog']");
    this.tableHeader = self.$(".MuiTableHead-root");
    this.headerCells = tableHeader.$$(".MuiTableCell-root");
    this.tableRows = self.$("tbody").$$("tr");
  }

  public SpendingTable() {
    super($("#spendings"));
    this.searchField = new SearchField();
    this.periodMenu = self.$("#period");
    this.currencyMenu = self.$("#currency");
    this.menuItems = $$(".MuiList-padding li");
    this.deleteBtn = self.$("#delete");
    this.popup = $("div[role='dialog']");
    this.tableHeader = self.$(".MuiTableHead-root");
    this.headerCells = tableHeader.$$(".MuiTableCell-root");
    this.tableRows = self.$("tbody").$$("tr");
  }

  public SpendingTable checkSpends(SpendJson... expectedSpends) {
    tableRows.should(spends(expectedSpends));
    return this;
  }
}