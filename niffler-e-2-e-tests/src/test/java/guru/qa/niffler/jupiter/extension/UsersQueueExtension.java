package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersQueueExtension implements ParameterResolver, AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "alex", null, null));
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

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome
    ) {
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == StaticUser.class
                && parameterContext.getParameter().isAnnotationPresent(UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        UserType userTypeAnnotation = parameterContext.getParameter().getAnnotation(UserType.class);
        Type type = userTypeAnnotation.value();

        Map<Type, StaticUser> userMap = getUserMap(extensionContext);

        if (userMap.containsKey(type)) {
            return userMap.get(type);
        }

        StaticUser user = takeUserFromQueue(type);
        userMap.put(type, user);

        return user;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);
        Object userMapObject = store.get(extensionContext.getUniqueId());

        if (userMapObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Type, StaticUser> userMap = (Map<Type, StaticUser>) userMapObject;

            for (Map.Entry<Type, StaticUser> entry : userMap.entrySet()) {
                returnUserToQueue(entry.getKey(), entry.getValue());
            }
            store.remove(extensionContext.getUniqueId());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Type, StaticUser> getUserMap(ExtensionContext context) {
        ExtensionContext.Store store = context.getStore(NAMESPACE);

        Object existingMap = store.get(context.getUniqueId());
        if (existingMap instanceof Map) {
            return (Map<Type, StaticUser>) existingMap;
        }

        Map<Type, StaticUser> newMap = new HashMap<>();
        store.put(context.getUniqueId(), newMap);
        return newMap;
    }

    private StaticUser takeUserFromQueue(Type type) {
        StaticUser user = switch (type) {
            case EMPTY -> EMPTY_USERS.poll();
            case WITH_FRIEND -> WITH_FRIEND_USERS.poll();
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.poll();
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.poll();
        };

        if (user == null) {
            throw new IllegalStateException("No available user for type: " + type);
        }

        return user;
    }

    private void returnUserToQueue(Type type, StaticUser user) {
        switch (type) {
            case EMPTY -> EMPTY_USERS.add(user);
            case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
        }
    }
}