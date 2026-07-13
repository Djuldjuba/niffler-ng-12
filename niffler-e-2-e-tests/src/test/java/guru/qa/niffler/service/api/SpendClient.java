package guru.qa.niffler.service.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public interface SpendClient {
  SpendJson createSpending(SpendJson spending);

  CategoryJson createCategory(CategoryJson category);

  Optional<CategoryJson> findByUsernameAndName(String username, String category);
}
