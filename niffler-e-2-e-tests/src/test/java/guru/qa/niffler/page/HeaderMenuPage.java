package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class HeaderMenuPage {

    private final SelenideElement profileBtn = $x("//button[@aria-label='Menu']");
    private final SelenideElement firendsMenuBtn = $(byText("Friends"));
    private final SelenideElement allPeopleMenuBtn = $(byText("All People"));

    public HeaderMenuPage clickProfileButton() {
        profileBtn.click();
        return this;
    }

    public FriendsPage clickFriendsMenuButton() {
        firendsMenuBtn.click();
        return new FriendsPage();
    }

    public AllPeoplePage clickAllPeopleMenuButton() {
        allPeopleMenuBtn.click();
        return new AllPeoplePage();
    }
}
