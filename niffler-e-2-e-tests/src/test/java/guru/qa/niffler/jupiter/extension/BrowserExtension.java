package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrowserExtension implements AfterEachCallback, TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserExtension.class);
    private static final String SCREENSHOTS_DIR = "build/screenshots/";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            boolean testFailed = context.getExecutionException().isPresent();

            if (testFailed) {
                captureScreenshot(context);
                capturePageSource(context);
                LOG.error("Test failed: {}. Screenshot and page source saved.",
                        context.getDisplayName());
            } else {
                LOG.info("Test passed: {}", context.getDisplayName());
            }
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            attachScreenshotToAllure();
            attachPageSourceToAllure();
            LOG.info("Allure attachments added for failed test: {}", context.getDisplayName());
        }
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    public byte[] attachScreenshotToAllure() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            try {
                return ((TakesScreenshot) WebDriverRunner.getWebDriver())
                        .getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                LOG.error("Failed to attach screenshot to Allure", e);
            }
        }
        return new byte[0];
    }

    @Attachment(value = "Page Source on failure", type = "text/html")
    public String attachPageSourceToAllure() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            try {
                return WebDriverRunner.getWebDriver().getPageSource();
            } catch (Exception e) {
                LOG.error("Failed to attach page source to Allure", e);
            }
        }
        return "No page source available";
    }

    private void captureScreenshot(ExtensionContext context) {
        try {
            String screenshotName = generateFileName(context, "screenshot");
            File screenshotFile = new File(SCREENSHOTS_DIR + screenshotName + ".png");
            Files.createDirectories(Paths.get(SCREENSHOTS_DIR));
            Selenide.screenshot(screenshotFile.getAbsolutePath());

            Allure.addAttachment(
                    "Screenshot: " + context.getDisplayName(),
                    "image/png",
                    Files.newInputStream(screenshotFile.toPath()),
                    ".png"
            );

            LOG.info("Screenshot saved: {}", screenshotFile.getAbsolutePath());

        } catch (IOException e) {
            LOG.error("Failed to save screenshot", e);
        }
    }

    private void capturePageSource(ExtensionContext context) {
        try {
            String sourceName = generateFileName(context, "pagesource");
            Path sourcePath = Paths.get(SCREENSHOTS_DIR + sourceName + ".html");

            String pageSource = WebDriverRunner.getWebDriver().getPageSource();
            Files.write(sourcePath, pageSource.getBytes());

            Allure.addAttachment(
                    "Page Source: " + context.getDisplayName(),
                    "text/html",
                    Files.newInputStream(sourcePath),
                    ".html"
            );

            LOG.info("Page source saved: {}", sourcePath.toAbsolutePath());

        } catch (IOException e) {
            LOG.error("Failed to save page source", e);
        }
    }

    private String generateFileName(ExtensionContext context, String prefix) {
        String className = context.getRequiredTestClass().getSimpleName();
        String methodName = context.getRequiredTestMethod().getName();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        return String.format("%s_%s_%s_%s",
                prefix,
                className,
                methodName,
                timestamp
        );
    }
}