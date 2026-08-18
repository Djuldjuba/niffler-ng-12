package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";
    private static final String USERS_TO_CLEANUP = "usersToCleanup";

    private final UsersDbClient usersDbClient = new UsersDbClient();

    @SuppressWarnings("unchecked")
    @Override
    public void beforeEach(ExtensionContext context) {
        Set<String> usersToDelete = (Set<String>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                USERS_TO_CLEANUP,
                key -> new HashSet<String>()
        );

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.createUser()) {
                        final String username;
                        if (!userAnno.username().isEmpty()) {
                            username = userAnno.username();
                        } else {
                            username = RandomDataUtils.randomUsername();
                        }
                        final UserJson user = new UserJson(username, DEFAULT_PASSWORD);
                        UserJson createdUser = usersDbClient.createUserWithAuthorities(user);

                        final TestData testData = new TestData(
                                DEFAULT_PASSWORD,
                                new ArrayList<>()
                        );

                        UserJson userWithTestData = createdUser.addTestData(testData);
                        setUser(userWithTestData);

                        if (userAnno.friends() > 0) {
                            createFriends(username, userAnno.friends());
                        }

                        if (userAnno.incomeInvitations() > 0) {
                            createIncomeInvitations(username, userAnno.incomeInvitations());
                        }

                        if (userAnno.outcomeInvitations() > 0) {
                            createOutcomeInvitations(username, userAnno.outcomeInvitations());
                        }

                        if (userAnno.cleanup()) {
                            usersToDelete.add(username);
                        }
                    }
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterEach(ExtensionContext context) {
        Set<String> usersToDelete = (Set<String>) context.getStore(NAMESPACE).get(USERS_TO_CLEANUP);
        if (usersToDelete != null) {
            for (String username : usersToDelete) {
                try {
                    usersDbClient.findUserByUsername(username)
                            .ifPresent(usersDbClient::deleteUser);
                } catch (Exception e) {

                }
            }
            usersToDelete.clear();
        }
    }

    private void createFriends(String username, int count) {
        Optional<UserdataUserJson> userOpt = usersDbClient.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found in userdata: " + username);
        }
        usersDbClient.createFriends(userOpt.get(), count);
    }

    private void createIncomeInvitations(String username, int count) {
        Optional<UserdataUserJson> userOpt = usersDbClient.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found in userdata: " + username);
        }
        usersDbClient.createIncomeInvitations(userOpt.get(), count);
    }

    private void createOutcomeInvitations(String username, int count) {
        Optional<UserdataUserJson> userOpt = usersDbClient.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found in userdata: " + username);
        }
        usersDbClient.createOutcomeInvitations(userOpt.get(), count);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdUser().orElseThrow(() -> new IllegalStateException("No user created"));
    }

    public static void setUser(UserJson testUser) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }

    public static Optional<UserJson> createdUser() {
        try {
            final ExtensionContext methodContext = TestMethodContextExtension.context();
            return Optional.ofNullable(methodContext.getStore(NAMESPACE)
                    .get(methodContext.getUniqueId(), UserJson.class));
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }
}