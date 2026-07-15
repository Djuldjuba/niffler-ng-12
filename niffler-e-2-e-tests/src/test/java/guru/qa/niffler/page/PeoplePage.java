package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;

import static com.codeborne.selenide.Selenide.$;

public class PeoplePage extends BasePage<PeoplePage> {

  public static final String URL = CFG.frontUrl() + "people/all";

  private final SelenideElement peopleTab;
  private final SelenideElement allTab;
  private final SearchField searchInput;
  private final SelenideElement peopleTable;
  private final SelenideElement pagePrevBtn;
  private final SelenideElement pageNextBtn;

  public PeoplePage(SelenideDriver driver) {
    super(driver);
    this.peopleTab = driver.$("a[href='/people/friends']");
    this.allTab = driver.$("a[href='/people/all']");
    this.searchInput = new SearchField(driver);
    this.peopleTable = driver.$("#all");
    this.pagePrevBtn = driver.$("#page-prev");
    this.pageNextBtn = driver.$("#page-next");
  }

  public PeoplePage() {
    super();
    this.peopleTab = $("a[href='/people/friends']");
    this.allTab = $("a[href='/people/all']");
    this.searchInput = new SearchField();
    this.peopleTable = $("#all");
    this.pagePrevBtn = $("#page-prev");
    this.pageNextBtn = $("#page-next");
  }
}