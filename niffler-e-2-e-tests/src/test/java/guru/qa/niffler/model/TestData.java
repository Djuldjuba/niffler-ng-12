package guru.qa.niffler.model;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    private String password;
    private List<String> categories = new ArrayList<>();

    public TestData() {
    }

    public TestData(String password) {
        this.password = password;
    }

    public TestData(String password, List<String> categories) {
        this.password = password;
        this.categories = categories;
    }

    public String password() {
        return password;
    }

    public TestData setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<String> getCategories() {
        return categories;
    }

    public TestData setCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public TestData addCategory(String category) {
        this.categories.add(category);
        return this;
    }
}