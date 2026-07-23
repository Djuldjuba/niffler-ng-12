package guru.qa.niffler.service;

import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.CategoryJson;

import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    Optional<CategoryJson> findCategoryById(UUID id);

    Optional<CategoryJson> findCategoryByUsernameAndName(String username, String name);

    Optional<SpendJson> findSpendById(UUID id);

    Optional<SpendJson> findSpendByUsernameAndDescription(String username, String description);

    void deleteSpend(SpendJson spend);

    void deleteCategory(CategoryJson category);
}

