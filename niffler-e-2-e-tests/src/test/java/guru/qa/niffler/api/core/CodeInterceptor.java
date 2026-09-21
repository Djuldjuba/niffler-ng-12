package guru.qa.niffler.api.core;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

public class CodeInterceptor implements Interceptor {

    @Nonnull
    @Override
    public Response intercept(@Nonnull Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());

        if (response.isRedirect()) {
            String location = response.header("Location");
            if (location != null && location.contains("code=")) {
                String code = StringUtils.substringAfter(location, "code=");
                if (code.contains("&")) {
                    code = StringUtils.substringBefore(code, "&");
                }
                ApiLoginExtension.setCode(code);
            }
        }
        return response;
    }
}