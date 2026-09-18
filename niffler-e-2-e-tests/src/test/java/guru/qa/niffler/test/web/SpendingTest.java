package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

  @Test
  @User(
          username = "duck",
          categories = @Category(name = "Коммуналка"),
          spendings = @Spending(
                  category = "Коммуналка",
                  description = "Коммунальные платежи",
                  amount = 12000
          )
  )
  void testWithCategoryAndSpending(CategoryJson category, SpendJson spending) {
    assertEquals("duck", category.username());
    assertEquals("duck", spending.username());
    assertEquals("Коммуналка", category.name());
    assertEquals(category.name(), spending.category().name());
    assertEquals("Коммунальные платежи", spending.description());
    assertEquals(12000, spending.amount());
  }
}