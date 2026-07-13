package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SelectField;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  public static final String URL = CFG.frontUrl() + "spending";

  private final Calendar calendar = new Calendar();
  private final SelectField currencySelect = new SelectField($("#currency"));

  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement categoryInput = $("#category");
  private final ElementsCollection categories = $$(".MuiChip-root");
  private final SelenideElement descriptionInput = $("#description");

  private final SelenideElement cancelBtn = $("#cancel");
  private final SelenideElement saveBtn = $("#save");
}
