package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

  @Test
  void getAllCategoriesShouldFilterByExcludeArchived(@Mock CategoryRepository categoryRepository) {
    String username = "duck";

    CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(UUID.randomUUID());
    archivedCategory.setName("Archived");
    archivedCategory.setUsername(username);
    archivedCategory.setArchived(true);

    CategoryEntity activeCategory = new CategoryEntity();
    activeCategory.setId(UUID.randomUUID());
    activeCategory.setName("Active");
    activeCategory.setUsername(username);
    activeCategory.setArchived(false);

    List<CategoryEntity> categories = List.of(archivedCategory, activeCategory);

    Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
            .thenReturn(categories);

    CategoryService categoryService = new CategoryService(categoryRepository);

    List<CategoryJson> resultWithExclude = categoryService.getAllCategories(username, true);

    assertEquals(1, resultWithExclude.size());
    assertEquals("Active", resultWithExclude.get(0).name());
    assertFalse(resultWithExclude.get(0).archived());

    List<CategoryJson> resultWithoutExclude = categoryService.getAllCategories(username, false);

    assertEquals(2, resultWithoutExclude.size());
  }

  @Test
  void updateShouldThrowTooManyCategoriesExceptionWhenUnarchivingWithMoreThanMaxCategories(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    UUID id = UUID.randomUUID();

    CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(id);
    archivedCategory.setUsername(username);
    archivedCategory.setName("Archived Category");
    archivedCategory.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(archivedCategory));
    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            id,
            "Unarchived Category",
            username,
            false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.update(categoryJson)
    );
    assertEquals("Can`t unarchive category for user: '" + username + "'", ex.getMessage());
  }

  @Test
  void updateShouldAllowUnarchivingWhenCategoriesCountIsLessThanMax(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    UUID id = UUID.randomUUID();

    CategoryEntity archivedCategory = new CategoryEntity();
    archivedCategory.setId(id);
    archivedCategory.setUsername(username);
    archivedCategory.setName("Archived Category");
    archivedCategory.setArchived(true);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(archivedCategory));
    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(5L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            id,
            "Unarchived Category",
            username,
            false
    );

    CategoryJson result = categoryService.update(categoryJson);

    assertFalse(result.archived());
    assertEquals("Unarchived Category", result.name());
    verify(categoryRepository).save(any(CategoryEntity.class));
  }

  @Test
  void updateShouldNotCheckMaxCategoriesWhenArchiving(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    UUID id = UUID.randomUUID();

    CategoryEntity activeCategory = new CategoryEntity();
    activeCategory.setId(id);
    activeCategory.setUsername(username);
    activeCategory.setName("Active Category");
    activeCategory.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
            .thenReturn(Optional.of(activeCategory));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            id,
            "Active Category",
            username,
            true
    );

    CategoryJson result = categoryService.update(categoryJson);

    assertTrue(result.archived());
    verify(categoryRepository, Mockito.never()).countByUsernameAndArchived(any(), anyBoolean());
  }

  @Test
  void saveShouldThrowTooManyCategoriesExceptionWhenExceedsMaxCategories(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(8L);

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            null,
            "New Category",
            username,
            false
    );

    TooManyCategoriesException ex = Assertions.assertThrows(
            TooManyCategoriesException.class,
            () -> categoryService.save(categoryJson)
    );
    assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());
  }

  @Test
  void saveShouldAllowAddingCategoryWhenLessThanMax(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    String categoryName = "New Category";

    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(3L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> {
              CategoryEntity entity = invocation.getArgument(0);
              entity.setId(UUID.randomUUID());
              return entity;
            });

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            null,
            categoryName,
            username,
            false
    );

    CategoryEntity result = categoryService.save(categoryJson);

    assertNotNull(result.getId());
    assertEquals(categoryName, result.getName());
    assertEquals(username, result.getUsername());
    assertFalse(result.isArchived());
    verify(categoryRepository).save(any(CategoryEntity.class));
  }

  @Test
  void saveShouldThrowInvalidCategoryNameExceptionWhenNameIsArchived(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    String categoryName = "Archived";

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            null,
            categoryName,
            username,
            false
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
            InvalidCategoryNameException.class,
            () -> categoryService.save(categoryJson)
    );
    assertEquals("Can`t add category with name: '" + categoryName + "'", ex.getMessage());
  }

  @Test
  void getOrSaveShouldReturnExistingCategoryIfFound(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    String categoryName = "Existing Category";

    CategoryEntity existingCategory = new CategoryEntity();
    existingCategory.setId(UUID.randomUUID());
    existingCategory.setName(categoryName);
    existingCategory.setUsername(username);
    existingCategory.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndName(eq(username), eq(categoryName)))
            .thenReturn(Optional.of(existingCategory));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            null,
            categoryName,
            username,
            false
    );

    CategoryEntity result = categoryService.getOrSave(categoryJson);

    assertEquals(existingCategory.getId(), result.getId());
    assertEquals(categoryName, result.getName());
    verify(categoryRepository, Mockito.never()).save(any(CategoryEntity.class));
  }

  @Test
  void getOrSaveShouldSaveNewCategoryIfNotFound(
          @Mock CategoryRepository categoryRepository
  ) {
    String username = "duck";
    String categoryName = "New Category";

    Mockito.when(categoryRepository.findByUsernameAndName(eq(username), eq(categoryName)))
            .thenReturn(Optional.empty());
    Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
            .thenReturn(3L);
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
            .thenAnswer(invocation -> {
              CategoryEntity entity = invocation.getArgument(0);
              entity.setId(UUID.randomUUID());
              return entity;
            });

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
            null,
            categoryName,
            username,
            false
    );

    CategoryEntity result = categoryService.getOrSave(categoryJson);

    assertNotNull(result.getId());
    assertEquals(categoryName, result.getName());
    verify(categoryRepository).save(any(CategoryEntity.class));
  }
}