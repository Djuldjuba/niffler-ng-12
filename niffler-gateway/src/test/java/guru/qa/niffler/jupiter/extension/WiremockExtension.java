package guru.qa.niffler.jupiter.extension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import guru.qa.niffler.jupiter.annotation.WiremockStubs;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.wiremock.grpc.GrpcExtensionFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class WiremockExtension implements BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback {

    private static WireMockServer server;
    private static int port;

    @Override
    public void beforeAll(ExtensionContext context) {
        URL resource = WiremockExtension.class.getClassLoader().getResource("wiremock");
        if (resource == null) {
            throw new RuntimeException("Папка 'wiremock' не найдена в src/test/resources!");
        }

        String rootPath;
        try {
            rootPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
                        .usingFilesUnderDirectory(rootPath)
                        .extensions(new GrpcExtensionFactory())
                        .notifier(new ConsoleNotifier(true))
        );
        server.start();
        port = server.port();
        WireMock.configureFor("localhost", port);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        server.resetAll();

        Optional<WiremockStubs> methodAnn = Optional.ofNullable(
                context.getRequiredTestMethod().getAnnotation(WiremockStubs.class));

        Optional<WiremockStubs> classAnn = context.getTestClass()
                .map(c -> c.getAnnotation(WiremockStubs.class));

        String[] files = methodAnn.map(WiremockStubs::value)
                .or(() -> classAnn.map(WiremockStubs::value))
                .orElse(new String[0]);

        for (String file : files) {
            String json = readResource("wiremock/mappings/" + file);
            StubMapping stub = StubMapping.buildFrom(json);
            server.addStubMapping(stub);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (server != null) {
            server.resetAll();
        }
    }

    private String readResource(String path) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Файл не найден в classpath: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static int getPort() {
        return port;
    }

    public static String getGrpcAddress() {
        return "static://localhost:" + port;
    }
}