package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLoginRest;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.api.AuthApiClient;
import guru.qa.niffler.service.api.SpendApiClient;
import guru.qa.niffler.service.api.UsersApiClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ApiLoginRestExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginRestExtension.class);

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final SpendApiClient spendClient = new SpendApiClient();
    private final UsersClient usersClient = new UsersApiClient();

    private static final String STATUS_FRIEND = "FRIEND";
    private static final String STATUS_INVITE_RECEIVED = "INVITE_RECEIVED";
    private static final String STATUS_INVITE_SENT = "INVITE_SENT";

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLoginRest.class)
                .ifPresent(apiLoginRest -> {
                    final UserJson userToLogin;
                    final Optional<UserJson> userFromUserExtension = UserExtension.createdUser();

                    if (apiLoginRest.username().isEmpty() || apiLoginRest.password().isEmpty()) {
                        if (userFromUserExtension.isEmpty()) {
                            throw new IllegalStateException("No user found in UserExtension and username/password not provided in @ApiLoginRest");
                        }
                        userToLogin = userFromUserExtension.get();
                    } else {
                        UserJson existingUser = new UserJson(apiLoginRest.username(), apiLoginRest.password());
                        if (userFromUserExtension.isPresent()) {
                            UserExtension.setUser(existingUser);
                        } else {
                            UserExtension.setUser(existingUser);
                        }
                        userToLogin = existingUser;
                    }

                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.password()
                    );
                    setToken(token);

                    UserJson enrichedUser = enrichUserWithAllData(userToLogin);
                    UserExtension.setUser(enrichedUser);
                });
    }

    private UserJson enrichUserWithAllData(UserJson user) {
        String username = user.username();

        List<CategoryJson> categories = spendClient.getCategories(username, false);
        List<SpendJson> spends = spendClient.getSpends(username, null, null, null);

        List<UserdataUserJson> friendsFromApi = usersClient.friends(username, null);

        List<UserdataUserJson> friends = new ArrayList<>();
        List<UserdataUserJson> incomeInvitations = new ArrayList<>();
        List<UserdataUserJson> outcomeInvitations = new ArrayList<>();

        for (UserdataUserJson u : friendsFromApi) {
            if (u.username().equals(username)) continue;

            String status = u.friendshipStatus();
            if (status == null) continue;

            switch (status) {
                case STATUS_FRIEND -> friends.add(u);
                case STATUS_INVITE_RECEIVED -> incomeInvitations.add(u);
                case STATUS_INVITE_SENT -> outcomeInvitations.add(u);
                default -> System.out.println("Unknown status: " + status + " for " + u.username());
            }
        }

        TestData testData = user.testData() != null ? user.testData() : new TestData();

        categories.stream().map(CategoryJson::name).forEach(testData::addCategory);
        testData.addSpends(spends);
        testData.addFriends(friends);
        testData.addInvitations(incomeInvitations);
        testData.addOutcomeInvitations(outcomeInvitations);

        return user.addTestData(testData);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            context.getStore(NAMESPACE).put("token", token);
        } catch (IllegalStateException e) {
            System.setProperty("niffler.test.token.rest", token);
        }
    }

    public static String getToken() {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            return context.getStore(NAMESPACE).get("token", String.class);
        } catch (IllegalStateException e) {
            return System.getProperty("niffler.test.token.rest");
        }
    }

    public static void setCode(String code) {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            context.getStore(NAMESPACE).put("code", code);
        } catch (IllegalStateException e) {
            System.setProperty("niffler.test.code.rest", code);
        }
    }

    public static String getCode() {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            return context.getStore(NAMESPACE).get("code", String.class);
        } catch (IllegalStateException e) {
            return System.getProperty("niffler.test.code.rest");
        }
    }
}