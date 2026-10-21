package guru.qa.niffler.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.utils.OAuthUtils;
import lombok.SneakyThrows;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), false, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    @Nonnull
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "/authorized";
        final String clientId = CFG.webClientId();

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        authApi.getLoginPage().execute();

        String csrfToken = ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN");
        if (csrfToken == null || csrfToken.isEmpty()) {
            throw new RuntimeException();
        }

        Response<Void> loginResponse = authApi.login(
                username,
                password,
                csrfToken
        ).execute();

        String location = loginResponse.headers().get("Location");
        if (location != null && location.contains("/oauth2/authorize")) {

            Response<Void> authorizeContinueResponse = authApi.followRedirect(location).execute();
            String finalLocation = authorizeContinueResponse.headers().get("Location");
            if (finalLocation != null && finalLocation.contains("code=")) {
                String code = OAuthUtils.extractCodeFromUrl(finalLocation);
                ApiLoginExtension.setCode(code);
            }
        }
        String code = ApiLoginExtension.getCode();
        if (code == null || code.isEmpty()) {
            throw new RuntimeException();
        }
        Response<JsonNode> tokenResponse = authApi.token(
                clientId,
                redirectUri,
                "authorization_code",
                code,
                codeVerifier
        ).execute();

        if (!tokenResponse.isSuccessful()) {
            String errorBody = tokenResponse.errorBody() != null ? tokenResponse.errorBody().string() : "null";
            throw new RuntimeException("Token request failed: " + tokenResponse.code() +
                    " - " + tokenResponse.message() +
                    ", body: " + errorBody);
        }

        JsonNode body = tokenResponse.body();
        if (body == null || !body.has("id_token")) {
            throw new RuntimeException();
        }
        return body.get("id_token").asText();
    }
}