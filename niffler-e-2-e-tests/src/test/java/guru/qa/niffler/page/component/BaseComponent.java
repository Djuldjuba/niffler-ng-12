package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

public abstract class BaseComponent<T extends BaseComponent<?>> {

  protected final SelenideDriver driver;
  protected final SelenideElement self;

  protected BaseComponent(SelenideDriver driver, SelenideElement self) {
    this.driver = driver;
    this.self = self;
  }

  protected BaseComponent(SelenideElement self) {
    this.driver = null;
    this.self = self;
  }

  @Nonnull
  public SelenideElement getSelf() {
    return self;
  }
}