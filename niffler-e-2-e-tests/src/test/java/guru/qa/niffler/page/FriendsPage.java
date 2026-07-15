package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;

import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = CFG.frontUrl() + "people/friends";

  private final SelenideElement peopleTab;
  private final SelenideElement allTab;
  private final SearchField searchInput;
  private final SelenideElement popup;
  private final SelenideElement requestsTable;
  private final SelenideElement friendsTable;
  private final SelenideElement pagePrevBtn;
  private final SelenideElement pageNextBtn;

  public FriendsPage(SelenideDriver driver) {
    super(driver);
    this.peopleTab = driver.$("a[href='/people/friends']");
    this.allTab = driver.$("a[href='/people/all']");
    this.searchInput = new SearchField(driver);
    this.popup = driver.$("div[role='dialog']");
    this.requestsTable = driver.$("#requests");
    this.friendsTable = driver.$("#friends");
    this.pagePrevBtn = driver.$("#page-prev");
    this.pageNextBtn = driver.$("#page-next");
  }

  public FriendsPage() {
    super();
    this.peopleTab = $("a[href='/people/friends']");
    this.allTab = $("a[href='/people/all']");
    this.searchInput = new SearchField();
    this.popup = $("div[role='dialog']");
    this.requestsTable = $("#requests");
    this.friendsTable = $("#friends");
    this.pagePrevBtn = $("#page-prev");
    this.pageNextBtn = $("#page-next");
  }
}