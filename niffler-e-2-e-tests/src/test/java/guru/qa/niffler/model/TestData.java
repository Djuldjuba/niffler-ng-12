package guru.qa.niffler.model;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    private String password;
    private List<String> categories = new ArrayList<>();
    private List<SpendJson> spends = new ArrayList<>();
    private List<UserdataUserJson> friends = new ArrayList<>();
    private List<UserdataUserJson> invitations = new ArrayList<>();      // входящие приглашения (INCOME)
    private List<UserdataUserJson> outcomeInvitations = new ArrayList<>(); // исходящие приглашения (OUTCOME)

    public TestData() {
    }

    public TestData(String password) {
        this.password = password;
    }

    public TestData(String password, List<String> categories) {
        this.password = password;
        this.categories = categories;
    }

    public TestData(String password, List<String> categories, List<SpendJson> spends) {
        this.password = password;
        this.categories = categories;
        this.spends = spends;
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

    public List<SpendJson> getSpends() {
        return spends;
    }

    public TestData setSpends(List<SpendJson> spends) {
        this.spends = spends;
        return this;
    }

    public List<UserdataUserJson> getFriends() {
        return friends;
    }

    public TestData setFriends(List<UserdataUserJson> friends) {
        this.friends = friends;
        return this;
    }

    public List<UserdataUserJson> getInvitations() {
        return invitations;
    }

    public TestData setInvitations(List<UserdataUserJson> invitations) {
        this.invitations = invitations;
        return this;
    }

    public List<UserdataUserJson> getOutcomeInvitations() {
        return outcomeInvitations;
    }

    public TestData setOutcomeInvitations(List<UserdataUserJson> outcomeInvitations) {
        this.outcomeInvitations = outcomeInvitations;
        return this;
    }

    public TestData addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    public TestData addSpend(SpendJson spend) {
        this.spends.add(spend);
        return this;
    }

    public TestData addSpends(List<SpendJson> spends) {
        this.spends.addAll(spends);
        return this;
    }

    public TestData addFriend(UserdataUserJson friend) {
        this.friends.add(friend);
        return this;
    }

    public TestData addFriends(List<UserdataUserJson> friends) {
        this.friends.addAll(friends);
        return this;
    }

    public TestData addInvitation(UserdataUserJson invitation) {
        this.invitations.add(invitation);
        return this;
    }

    public TestData addInvitations(List<UserdataUserJson> invitations) {
        this.invitations.addAll(invitations);
        return this;
    }

    public TestData addOutcomeInvitation(UserdataUserJson invitation) {
        this.outcomeInvitations.add(invitation);
        return this;
    }

    public TestData addOutcomeInvitations(List<UserdataUserJson> invitations) {
        this.outcomeInvitations.addAll(invitations);
        return this;
    }

    public boolean hasFriends() {
        return friends != null && !friends.isEmpty();
    }

    public boolean hasInvitations() {
        return invitations != null && !invitations.isEmpty();
    }

    public boolean hasOutcomeInvitations() {
        return outcomeInvitations != null && !outcomeInvitations.isEmpty();
    }
}