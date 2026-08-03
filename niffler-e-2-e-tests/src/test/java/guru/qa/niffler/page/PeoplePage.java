package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage extends BasePage<PeoplePage> {

  public static final String URL = CFG.frontUrl() + "people/all";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");

  private final SearchField searchInput = new SearchField();

  private final SelenideElement peopleTable = $("#all");
  private final SelenideElement pagePrevBtn = $("#page-prev");
  private final SelenideElement pageNextBtn = $("#page-next");
}
