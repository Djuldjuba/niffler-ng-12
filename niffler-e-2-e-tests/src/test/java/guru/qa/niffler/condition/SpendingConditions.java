package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendingConditions {

    private static final SimpleDateFormat DATE_FORMAT_EXPECTED =
            new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

    @Nonnull
    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        return new WebElementsCondition() {

            private final List<String> expectedDescriptions = buildExpectedDescriptions(expectedSpends);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (expectedSpends.length != elements.size()) {
                    final String message = String.format(
                            "Spends count mismatch (expected: %s, actual: %s)",
                            expectedSpends.length, elements.size()
                    );
                    return rejected(message, elements);
                }

                final List<String> actualDescriptions = new ArrayList<>();
                final List<String> errors = new ArrayList<>();

                for (int i = 0; i < elements.size(); i++) {
                    final WebElement row = elements.get(i);
                    final SpendJson expectedSpend = expectedSpends[i];

                    List<WebElement> cells = row.findElements(By.cssSelector("td"));

                    if (cells.size() < 6) {
                        errors.add(String.format(
                                "Row %d has insufficient cells (expected at least 6, actual: %s)",
                                i + 1, cells.size()
                        ));
                        continue;
                    }

                    String actualCategory = cells.get(1).getText().trim();
                    String actualAmount = cells.get(2).getText().trim();
                    String actualDescription = cells.get(3).getText().trim();
                    String actualDate = cells.get(4).getText().trim();

                    String actualAmountCleaned = actualAmount
                            .replaceAll("[₽$€]", "")
                            .replace(" ", "")
                            .replace(",", ".");

                    double actualAmountValue;
                    try {
                        actualAmountValue = Double.parseDouble(actualAmountCleaned);
                    } catch (NumberFormatException e) {
                        actualAmountValue = 0;
                    }

                    String normalizedDate = actualDate.replaceFirst(" 0(\\d)", " $1");

                    String actualNormalized = String.format("%s|%s|%s|%s",
                            actualCategory, actualAmountValue, actualDescription, normalizedDate
                    );
                    actualDescriptions.add(actualNormalized);

                    String expectedNormalized = String.format("%s|%s|%s|%s",
                            expectedSpend.category().name(),
                            expectedSpend.amount(),
                            expectedSpend.description() != null ? expectedSpend.description() : "",
                            DATE_FORMAT_EXPECTED.format(expectedSpend.spendDate())
                    );


                    if (!actualCategory.equals(expectedSpend.category().name())) {
                        errors.add(String.format(
                                "Row %d - Category mismatch (expected: %s, actual: %s)",
                                i + 1, expectedSpend.category().name(), actualCategory
                        ));
                    }

                    if (Math.abs(actualAmountValue - expectedSpend.amount()) > 0.001) {
                        errors.add(String.format(
                                "Row %d - Amount mismatch (expected: %s, actual: %s)",
                                i + 1, expectedSpend.amount(), actualAmountValue
                        ));
                    }

                    String expectedDescription = expectedSpend.description() != null ?
                            expectedSpend.description() : "";
                    if (!actualDescription.equals(expectedDescription)) {
                        errors.add(String.format(
                                "Row %d - Description mismatch (expected: %s, actual: %s)",
                                i + 1, expectedDescription, actualDescription
                        ));
                    }

                    String expectedDate = DATE_FORMAT_EXPECTED.format(expectedSpend.spendDate());
                    if (!normalizedDate.equals(expectedDate)) {
                        errors.add(String.format(
                                "Row %d - Date mismatch (expected: %s, actual: %s)",
                                i + 1, expectedDate, actualDate
                        ));
                    }
                }

                if (!errors.isEmpty()) {
                    String allErrors = String.join("\n", errors);
                    String message = String.format(
                            "Spends validation failed:\nExpected: %s\nActual: %s\n\nErrors:\n%s",
                            expectedDescriptions, actualDescriptions, allErrors
                    );
                    return rejected(message, actualDescriptions);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expectedDescriptions.toString();
            }

            private List<String> buildExpectedDescriptions(SpendJson[] spends) {
                List<String> descriptions = new ArrayList<>();
                for (SpendJson spend : spends) {
                    String date = spend.spendDate() != null ?
                            DATE_FORMAT_EXPECTED.format(spend.spendDate()) : "N/A";
                    String desc = String.format("%s|%s|%s|%s",
                            spend.category().name(),
                            spend.amount(),
                            spend.description() != null ? spend.description() : "",
                            date
                    );
                    descriptions.add(desc);
                }
                return descriptions;
            }
        };
    }
}