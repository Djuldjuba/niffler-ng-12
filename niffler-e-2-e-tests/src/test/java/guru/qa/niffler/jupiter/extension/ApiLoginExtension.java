package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.api.AuthApiClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final Optional<UserJson> userFromUserExtension = UserExtension.createdUser();

                    if (apiLogin.username().isEmpty() || apiLogin.password().isEmpty()) {
                        if (userFromUserExtension.isEmpty()) {
                            throw new IllegalStateException();
                        }
                        userToLogin = userFromUserExtension.get();
                    } else {
                        UserJson fakeUser = new UserJson(apiLogin.username(), apiLogin.password());
                        if (userFromUserExtension.isPresent()) {
                            throw new IllegalStateException();
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.password()
                    );

                    setToken(token);

                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                new Cookie(
                                        "JSESSIONID",
                                        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                                )
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getToken();
    }

    public static void setToken(String token) {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            context.getStore(NAMESPACE).put("token", token);
        } catch (IllegalStateException e) {
            System.setProperty("niffler.test.token", token);
        }
    }

    public static String getToken() {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            return context.getStore(NAMESPACE).get("token", String.class);
        } catch (IllegalStateException e) {
            return System.getProperty("niffler.test.token");
        }
    }

    public static void setCode(String code) {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            context.getStore(NAMESPACE).put("code", code);
        } catch (IllegalStateException e) {
            System.setProperty("niffler.test.code", code);
        }
    }

    public static String getCode() {
        try {
            ExtensionContext context = TestMethodContextExtension.context();
            return context.getStore(NAMESPACE).get("code", String.class);
        } catch (IllegalStateException e) {
            return System.getProperty("niffler.test.code");
        }
    }
}