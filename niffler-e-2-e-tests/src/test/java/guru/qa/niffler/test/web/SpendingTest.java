package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

  @Test
  @User(
          username = "duck",
          categories = @Category,
          spendings = @Spending
  )
  void testWithCategoryAndSpending(CategoryJson category, SpendJson spending) {
    System.out.println("Category: " + category);
    System.out.println("Spending: " + spending);
  }
}
