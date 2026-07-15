package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.ProfilePage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

  private final SelenideElement mainPageLink;
  private final SelenideElement addSpendingBtn;
  private final SelenideElement menuBtn;
  private final SelenideElement menu;
  private final ElementsCollection menuItems;

  public Header(SelenideDriver driver) {
    super(driver, driver.$("#root header"));
    this.mainPageLink = self.$("a[href*='/main']");
    this.addSpendingBtn = self.$("a[href*='/spending']");
    this.menuBtn = self.$("button");
    this.menu = driver.$("ul[role='menu']");
    this.menuItems = menu.$$("li");
  }

  public Header() {
    super($("#root header"));
    this.mainPageLink = self.$("a[href*='/main']");
    this.addSpendingBtn = self.$("a[href*='/spending']");
    this.menuBtn = self.$("button");
    this.menu = $("ul[role='menu']");
    this.menuItems = menu.$$("li");
  }

  public FriendsPage toFriendsPage() {
    menuBtn.click();
    menuItems.find(text("Friends")).click();
    return driver != null ? new FriendsPage(driver) : new FriendsPage();
  }

  public PeoplePage toAllPeoplesPage() {
    menuBtn.click();
    menuItems.find(text("All People")).click();
    return driver != null ? new PeoplePage(driver) : new PeoplePage();
  }

  public ProfilePage toProfilePage() {
    menuBtn.click();
    menuItems.find(text("Profile")).click();
    return driver != null ? new ProfilePage(driver) : new ProfilePage();
  }

  public LoginPage signOut() {
    menuBtn.click();
    menuItems.find(text("Sign out")).click();
    return driver != null ? new LoginPage(driver) : new LoginPage();
  }

  public EditSpendingPage addSpendingPage() {
    addSpendingBtn.click();
    return driver != null ? new EditSpendingPage(driver) : new EditSpendingPage();
  }

  public MainPage toMainPage() {
    mainPageLink.click();
    return driver != null ? new MainPage(driver) : new MainPage();
  }
}