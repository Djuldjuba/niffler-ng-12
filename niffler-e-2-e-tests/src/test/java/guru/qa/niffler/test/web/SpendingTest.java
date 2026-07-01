package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.api.SpendApiClient;
import org.junit.jupiter.api.Test;

public class SpendingTest {

  private static final Config CFG = Config.getInstance();
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Test
  @Spending(
          username = "duck",
          category = "Обучение",
          description = "Niffler 12 поток!",
          amount = 119000
  )
  void spendingDescriptionShouldBeEditedByTableAction2(SpendJson spendJson) {
    final String newDescription = "Niffler - финальный поток";

    SpendJson createdSpend = spendApiClient.createSpending(spendJson);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login("duck", "12345")
            .editSpending(createdSpend.description())
            .setNewSpendingDescription(newDescription)
            .save()
            .checkThatTableContains(newDescription);
  }
}