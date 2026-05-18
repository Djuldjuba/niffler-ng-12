package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage extends BasePage {

    private final ElementsCollection friendsNamesList = $$x("//*[@id='friends']//p[1]");
    private final SelenideElement noFriendsText = $x("//*[@id='simple-tabpanel-friends']//p[1]");
    private final ElementsCollection incomeFriendsNamesList = $$x("//*[@id='requests']//p[1]");

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
