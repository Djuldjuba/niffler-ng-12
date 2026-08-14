package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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
}
