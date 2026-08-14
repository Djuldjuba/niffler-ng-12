package guru.qa.niffler.api.core;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeCookieStore implements CookieJar {

    public static final ThreadSafeCookieStore INSTANCE = new ThreadSafeCookieStore();

    private final Map<String, Map<String, Cookie>> cookies = new ConcurrentHashMap<>();

    @Nonnull
    @Override
    public List<Cookie> loadForRequest(@Nonnull HttpUrl httpUrl) {
        String host = httpUrl.host();
        Map<String, Cookie> hostCookies = cookies.get(host);
        return hostCookies != null
                ? new ArrayList<>(hostCookies.values())
                : new ArrayList<>();
    }

    @Override
    public void saveFromResponse(@Nonnull HttpUrl httpUrl, @Nonnull List<Cookie> responseCookies) {
        String host = httpUrl.host();
        Map<String, Cookie> hostCookies = cookies.computeIfAbsent(
                host,
                key -> new ConcurrentHashMap<>()
        );

        for (Cookie cookie : responseCookies) {
            hostCookies.put(cookie.name(), cookie);
        }
    }

    public String cookieValue(String name) {
        return cookies.values().stream()
                .map(map -> map.get(name))
                .filter(Objects::nonNull)
                .map(Cookie::value)
                .findFirst()
                .orElse(null);
    }
}