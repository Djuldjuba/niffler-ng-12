package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome
    ) {}

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("alex", "12345", null, "bee", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "bill"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY,
            WITH_FRIEND,
            WITH_INCOME_REQUEST,
            WITH_OUTCOME_REQUEST
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .forEach(parameter -> {
                    UserType userType = parameter.getAnnotation(UserType.class);
                    UserType.Type requiredType = userType.value();

                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();

                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = getUserFromQueue(requiredType);
                    }

                    Allure.getLifecycle().updateTestCase(testCase -> {
                        testCase.setStart(new Date().getTime());
                    });

                    Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                            .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());

                    user.ifPresentOrElse(
                            u -> userMap.put(userType, u),
                            () -> {
                                throw new IllegalStateException(
                                        String.format("Can't find user of type %s after 30 sec", requiredType)
                                );
                            }
                    );
                });
    }

    private Optional<StaticUser> getUserFromQueue(UserType.Type type) {
        switch (type) {
            case EMPTY:
                return Optional.ofNullable(EMPTY_USERS.poll());
            case WITH_FRIEND:
                return Optional.ofNullable(WITH_FRIEND_USERS.poll());
            case WITH_INCOME_REQUEST:
                return Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
            case WITH_OUTCOME_REQUEST:
                return Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }

    private void returnUserToQueue(StaticUser user) {
        if (user.friend() != null) {
            WITH_FRIEND_USERS.add(user);
        } else if (user.income() != null) {
            WITH_INCOME_REQUEST_USERS.add(user);
        } else if (user.outcome() != null) {
            WITH_OUTCOME_REQUEST_USERS.add(user);
        } else {
            EMPTY_USERS.add(user);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterEach(ExtensionContext context) {
        Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);

        if (userMap != null) {
            for (StaticUser user : userMap.values()) {
                returnUserToQueue(user);
            }
            context.getStore(NAMESPACE).remove(context.getUniqueId());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);

        UserType userType = parameterContext.getParameter().getAnnotation(UserType.class);
        return userMap.get(userType);
    }
}