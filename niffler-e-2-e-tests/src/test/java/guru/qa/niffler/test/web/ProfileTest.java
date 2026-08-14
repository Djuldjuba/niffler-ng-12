package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@ExtendWith({BrowserExtension.class, TestMethodContextExtension.class})
public class ProfileTest {

    @Test
    @ApiLogin(username = "duck", password = "12345")
    void activeCategoryShouldPresentInCategoriesList() {
        final String newName = randomName();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPhotoFromClasspath("img/photo.png")
                .setName(newName)
                .submitProfile()
                .checkAlert("Profile successfully updated");
    }

    @Test
    @ApiLogin(username = "duck", password = "12345")
    void shouldAddNewCategory() {
        String newCategory = randomCategoryName();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .addCategory(newCategory)
                .checkAlert("You've added new category:")
                .checkCategoryExists(newCategory);
    }
}
