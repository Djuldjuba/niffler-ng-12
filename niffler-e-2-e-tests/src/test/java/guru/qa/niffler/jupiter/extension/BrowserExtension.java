package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

  private static final ThreadLocal<List<SelenideDriver>> threadLocalDrivers = ThreadLocal.withInitial(ArrayList::new);

  @Override
  public void beforeEach(ExtensionContext context) {
    SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
            .savePageSource(false)
            .screenshots(false)
    );
    threadLocalDrivers.get().clear();
  }

  @Override
  public void afterEach(ExtensionContext context) {
    List<SelenideDriver> drivers = threadLocalDrivers.get();
    for (SelenideDriver driver : drivers) {
      try {
        if (driver != null && driver.hasWebDriverStarted()) {
          driver.close();
        }
      } catch (Exception e) {

      }
    }
    drivers.clear();
    threadLocalDrivers.remove();
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  private void doScreenshot() {
    List<SelenideDriver> drivers = threadLocalDrivers.get();
    for (SelenideDriver driver : drivers) {
      try {
        if (driver != null && driver.hasWebDriverStarted()) {
          Allure.addAttachment(
                  "Screen on fail for browser: " + driver.config().browser(),
                  new ByteArrayInputStream(
                          ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                  )
          );
        }
      } catch (Exception e) {

      }
    }
  }

  public static void registerDriver(SelenideDriver driver) {
    List<SelenideDriver> drivers = threadLocalDrivers.get();
    if (!drivers.contains(driver)) {
      drivers.add(driver);
    }
  }
}