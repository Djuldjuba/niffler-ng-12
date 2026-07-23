package guru.qa.niffler.test.web;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.OAuthUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FakeTest {

    private static final Config CFG = Config.getInstance();
    private AuthApi authApi;

    @BeforeEach
    void setUp() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cookieJar(ThreadSafeCookieStore.INSTANCE)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor(new CodeInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CFG.authUrl())
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);
    }

    @Test
    void shouldReceiveIdTokenAfterOAuthFlow() throws Exception {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

        String redirectUri = CFG.frontUrl() + "/authorized";
        String clientId = CFG.webClientId();

        Response<Void> authorize = authApi.authorize(
                "code", clientId, "openid", redirectUri, codeChallenge, "S256"
        ).execute();

        assertEquals(302, authorize.code());
        assertTrue(authorize.headers().get("Location").contains("/login"));

        authApi.getLoginPage().execute();
        String csrf = ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN");
        assertNotNull(csrf);

        Response<Void> login = authApi.login("duck", "12345", csrf).execute();
        assertEquals(302, login.code());

        Response<Void> redirect = authApi.followRedirect(login.headers().get("Location")).execute();
        String code = OAuthUtils.extractCodeFromUrl(redirect.headers().get("Location"));
        assertNotNull(code);

        Response<JsonNode> token = authApi.token(
                clientId, redirectUri, "authorization_code", code, codeVerifier
        ).execute();

        assertTrue(token.isSuccessful());
        assertTrue(token.body().has("id_token"));
        assertTrue(token.body().has("access_token"));

        String idToken = token.body().get("id_token").asText();
        assertNotNull(idToken);
        assertFalse(idToken.isEmpty());
    }
}
