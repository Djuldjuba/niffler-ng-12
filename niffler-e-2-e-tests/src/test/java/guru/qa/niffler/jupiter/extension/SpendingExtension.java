package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        CategoryJson existingCategory = CategoryExtension.getCreatedCategory(context);

        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(userAnno -> {

            String username = userAnno.username().equals("RANDOM")
                    ? RandomDataUtils.randomUsername()
                    : userAnno.username();

            Spending[] spendings = userAnno.spendings();
            if (spendings.length > 0) {
                Spending firstSpending = spendings[0];

                String categoryName = firstSpending.category().equals("RANDOM")
                        ? RandomDataUtils.randomCategoryName()
                        : firstSpending.category();

                String description = firstSpending.description().equals("RANDOM")
                        ? RandomDataUtils.randomSentence(5)
                        : firstSpending.description();

                double amount = firstSpending.amount() == -1
                        ? RandomDataUtils.randomAmount(100, 100000)
                        : firstSpending.amount();

                CategoryJson categoryForSpending;

                if (existingCategory != null && existingCategory.name().equals(categoryName)) {
                    categoryForSpending = existingCategory;
                } else {
                    categoryForSpending = spendClient.createCategory(
                            new CategoryJson(null, categoryName, username, false)
                    );
                }

                final SpendJson created = spendClient.createSpending(
                        new SpendJson(
                                null,
                                new Date(),
                                categoryForSpending,
                                firstSpending.currency(),
                                amount,
                                description,
                                username
                        )
                );
                context.getStore(NAMESPACE).put(context.getUniqueId(), created);
            }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
