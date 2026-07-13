package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = CFG.frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SearchField searchInput = new SearchField();
  private final SelenideElement popup = $("div[role='dialog']");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement pagePrevBtn = $("#page-prev");
  private final SelenideElement pageNextBtn = $("#page-next");

}
