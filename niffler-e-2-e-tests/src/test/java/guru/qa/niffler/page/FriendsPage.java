package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage extends BasePage {

    private final ElementsCollection friendsNamesList = $$("#friends tr td:first-child p.MuiTypography-body1");
    private final SelenideElement noFriendsText = $("#simple-tabpanel-friends p.MuiTypography-h6");
    private final ElementsCollection incomeFriendsNamesList = $$("#requests tr p.MuiTypography-root.MuiTypography-body1");

    public FriendsPage checkFriendShouldBeVisible(String name) {
        friendsNamesList.findBy(text(name)).shouldBe(visible);
        return this;
    }

    public FriendsPage checkNoFriendText() {
        noFriendsText.shouldHave(text("There are no users yet"));
        return this;
    }

    public FriendsPage checkIncomeFriendShouldBeVisible(String name) {
        incomeFriendsNamesList.findBy(text(name)).shouldBe(visible);
        return this;
    }
}
