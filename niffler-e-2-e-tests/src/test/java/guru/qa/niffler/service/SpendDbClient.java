package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.spendJdbcUrl());

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);

            if (spendEntity.getCategory().getId() == null) {
                Optional<CategoryEntity> existingCategory = spendRepository.findCategoryByUsernameAndSpendName(
                        spendEntity.getCategory().getUsername(),
                        spendEntity.getCategory().getName()
                );

                CategoryEntity categoryEntity = existingCategory.orElseGet(() ->
                        spendRepository.createCategory(spendEntity.getCategory())
                );
                spendEntity.setCategory(categoryEntity);
            }

            SpendEntity createdSpend = spendRepository.create(spendEntity);
            return SpendJson.fromEntity(createdSpend);
        });
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            SpendEntity updatedSpend = spendRepository.update(spendEntity);
            return SpendJson.fromEntity(updatedSpend);
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            CategoryEntity createdCategory = spendRepository.createCategory(categoryEntity);
            return CategoryJson.fromEntity(createdCategory);
        });
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        return spendRepository.findCategoryById(id)
                .map(CategoryJson::fromEntity);
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndName(String username, String name) {
        return spendRepository.findCategoryByUsernameAndSpendName(username, name)
                .map(CategoryJson::fromEntity);
    }

    @Override
    public Optional<SpendJson> findSpendById(UUID id) {
        return spendRepository.findById(id)
                .map(SpendJson::fromEntity);
    }

    @Override
    public Optional<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
        return spendRepository.findByUsernameAndSpendDescription(username, description)
                .map(SpendJson::fromEntity);
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            spendRepository.remove(spendEntity);
            return null;
        });
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            spendRepository.removeCategory(categoryEntity);
            return null;
        });
    }
}
