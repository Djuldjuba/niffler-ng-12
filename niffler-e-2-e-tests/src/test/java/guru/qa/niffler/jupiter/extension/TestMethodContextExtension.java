package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class TestMethodContextExtension implements TestInstancePostProcessor {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestMethodContextExtension.class);
    private static ExtensionContext currentContext;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        currentContext = context;
        context.getStore(NAMESPACE).put("context", context);
    }

    public static ExtensionContext context() {
        if (currentContext == null) {
            throw new IllegalStateException("No test context available");
        }
        return currentContext;
    }
}