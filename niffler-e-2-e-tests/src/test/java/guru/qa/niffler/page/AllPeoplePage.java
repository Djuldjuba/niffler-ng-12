package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class AllPeoplePage extends BasePage {

    public AllPeoplePage shouldVisibleOutcomeFriend(String name) {
        waitingStatusForFriend(name).shouldBe(visible);
        return this;
    }

    public SelenideElement waitingStatusForFriend(String name) {
        return $x(String.format("//*[@id='all']//*[text()='%s']/ancestor::tbody//span[text()='Waiting...']", name));
    }
}
