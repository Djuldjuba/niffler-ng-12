package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";

    private final UsersDbClient usersDbClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.createUser()) {
                        final String username = RandomDataUtils.randomUsername();
                        final UserJson user = new UserJson(username, DEFAULT_PASSWORD);
                        usersDbClient.createUserWithAuthorities(user);

                        final TestData testData = new TestData(
                                DEFAULT_PASSWORD,
                                new ArrayList<>()
                        );

                        setUser(user.addTestData(testData));
                    }
                });
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
        final ExtensionContext methodContext = TestMethodContextExtension.context();
        return Optional.ofNullable(methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), UserJson.class));
    }
}