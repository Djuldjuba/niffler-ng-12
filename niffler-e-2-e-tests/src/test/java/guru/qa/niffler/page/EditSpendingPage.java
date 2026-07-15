package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SelectField;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  public static final String URL = CFG.frontUrl() + "spending";

  private final Calendar calendar;
  private final SelectField currencySelect;
  private final SelenideElement amountInput;
  private final SelenideElement categoryInput;
  private final ElementsCollection categories;
  private final SelenideElement descriptionInput;
  private final SelenideElement cancelBtn;
  private final SelenideElement saveBtn;

  public EditSpendingPage(SelenideDriver driver) {
    super(driver);
    this.calendar = new Calendar(driver);
    this.currencySelect = new SelectField(driver.$("#currency"));
    this.amountInput = driver.$("#amount");
    this.categoryInput = driver.$("#category");
    this.categories = driver.$$(".MuiChip-root");
    this.descriptionInput = driver.$("#description");
    this.cancelBtn = driver.$("#cancel");
    this.saveBtn = driver.$("#save");
  }

  public EditSpendingPage() {
    super();
    this.calendar = new Calendar();
    this.currencySelect = new SelectField($("#currency"));
    this.amountInput = $("#amount");
    this.categoryInput = $("#category");
    this.categories = $$(".MuiChip-root");
    this.descriptionInput = $("#description");
    this.cancelBtn = $("#cancel");
    this.saveBtn = $("#save");
  }
}