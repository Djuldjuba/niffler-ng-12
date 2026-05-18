package guru.qa.niffler.page;

public abstract class BasePage {

    protected final HeaderMenuPage headerMenuPage;

    public BasePage() {
        this.headerMenuPage = new HeaderMenuPage();
    }

    public HeaderMenuPage clickProfileButton() {
        return headerMenuPage.clickProfileButton();
    }
}
