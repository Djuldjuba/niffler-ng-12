package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SelectField;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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
