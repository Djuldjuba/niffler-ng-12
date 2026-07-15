package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage<ProfilePage> {

  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement avatar;
  private final SelenideElement userName;
  private final SelenideElement nameInput;
  private final SelenideElement photoInput;
  private final SelenideElement submitButton;
  private final SelenideElement categoryInput;
  private final SelenideElement archivedSwitcher;
  private final ElementsCollection bubbles;
  private final ElementsCollection bubblesArchived;

  public ProfilePage(SelenideDriver driver) {
    super(driver);
    this.avatar = driver.$("#image__input").parent().$("img");
    this.userName = driver.$("#username");
    this.nameInput = driver.$("#name");
    this.photoInput = driver.$("input[type='file']");
    this.submitButton = driver.$("button[type='submit']");
    this.categoryInput = driver.$("input[name='category']");
    this.archivedSwitcher = driver.$(".MuiSwitch-input");
    this.bubbles = driver.$$(".MuiChip-filled.MuiChip-colorPrimary");
    this.bubblesArchived = driver.$$(".MuiChip-filled.MuiChip-colorDefault");
  }

  public ProfilePage() {
    super();
    this.avatar = $("#image__input").parent().$("img");
    this.userName = $("#username");
    this.nameInput = $("#name");
    this.photoInput = $("input[type='file']");
    this.submitButton = $("button[type='submit']");
    this.categoryInput = $("input[name='category']");
    this.archivedSwitcher = $(".MuiSwitch-input");
    this.bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    this.bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
  }

  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }

  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }

  public ProfilePage checkThatPageLoaded() {
    userName.should(visible);
    return this;
  }
}