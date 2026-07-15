package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class MainPage extends BasePage<MainPage> {

  public static final String URL = CFG.frontUrl() + "main";

  protected final Header header;
  protected final SpendingTable spendingTable;
  protected final StatComponent statComponent;
  protected final SelenideElement h1;

  public MainPage(SelenideDriver driver) {
    super(driver);
    this.header = new Header(driver);
    this.spendingTable = new SpendingTable(driver);
    this.statComponent = new StatComponent(driver);
    this.h1 = driver.$("h1");
  }

  public MainPage() {
    super();
    this.header = new Header();
    this.spendingTable = new SpendingTable();
    this.statComponent = new StatComponent();
    this.h1 = com.codeborne.selenide.Selenide.$("h1");
  }

  public Header getHeader() {
    return header;
  }

  public SpendingTable getSpendingTable() {
    spendingTable.getSelf().scrollIntoView(true);
    return spendingTable;
  }

  public StatComponent getStatComponent() {
    return statComponent;
  }

  public MainPage checkThatPageLoaded() {
    header.getSelf().should(visible).shouldHave(text("Niffler"));
    statComponent.getSelf().should(visible).shouldHave(text("Statistics"));
    spendingTable.getSelf().should(visible).shouldHave(text("History of Spendings"));
    h1.shouldHave(text("Niffler"));
    return this;
  }
}