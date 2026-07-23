package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage extends BasePage {

    public AllPeoplePage shouldVisibleOutcomeFriend(String name) {
        waitingStatusForFriend(name).shouldBe(visible);
        return this;
    }

    public SelenideElement waitingStatusForFriend(String name) {
        return $$("#all tr")
                .filterBy(Condition.text(name))
                .first()
                .find("td .MuiChip-root span.MuiChip-label")
                .shouldHave(Condition.text("Waiting..."));
    }
}
