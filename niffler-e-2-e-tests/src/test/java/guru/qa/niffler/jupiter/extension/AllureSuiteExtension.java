package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

public class AllureSuiteExtension implements SuiteExtension {

    private static final Logger log = LoggerFactory.getLogger(AllureSuiteExtension.class);

    private static final String PROJECT_ID = "niffler-ng";
    private static final String RESULTS_DIR = System.getProperty(
            "allure.results.directory",
            "build/allure-results"
    );

    @Override
    public void afterSuite() {
        String baseUrl = System.getenv("ALLURE_DOCKER_API");

        if (baseUrl == null || baseUrl.isBlank()) {
            log.info("ALLURE_DOCKER_API не задан, пропускаю отправку результатов");
            return;
        }

        baseUrl = baseUrl.replaceAll("/+$", "");
        String apiUrl = baseUrl + "/allure-docker-service";

        log.info("Allure Suite Extension: отправка результатов в {}", apiUrl);

        try {
            createProject(apiUrl);
            sendResults(apiUrl);
            generateReport(apiUrl);
            log.info("Allure отчёт успешно отправлен и сгенерирован");
        } catch (Exception e) {
            log.error("Ошибка при отправке результатов в Allure", e);
        }
    }

    private void createProject(String apiUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String body = "{\"id\":\"" + PROJECT_ID + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/projects"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Создание проекта {}: {}", PROJECT_ID, response.statusCode());
    }

    private void sendResults(String apiUrl) throws IOException, InterruptedException {
        Path resultsPath = Paths.get(RESULTS_DIR);

        if (!Files.exists(resultsPath)) {
            log.info("Папка {} не найдена, пропускаю", RESULTS_DIR);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode resultsArray = mapper.createArrayNode();

        try (Stream<Path> files = Files.list(resultsPath)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                try {
                    byte[] content = Files.readAllBytes(file);
                    if (content.length == 0) return;

                    String base64 = Base64.getEncoder().encodeToString(content);

                    ObjectNode fileNode = mapper.createObjectNode();
                    fileNode.put("file_name", file.getFileName().toString());
                    fileNode.put("content_base64", base64);

                    resultsArray.add(fileNode);
                } catch (IOException e) {
                    log.warn("Не удалось прочитать файл {}: {}", file, e.getMessage());
                }
            });
        }

        if (resultsArray.isEmpty()) {
            log.info("Нет файлов для отправки в {}", RESULTS_DIR);
            return;
        }

        root.set("results", resultsArray);
        String json = mapper.writeValueAsString(root);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/send-results?project_id=" + PROJECT_ID))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Отправка результатов: {}", response.statusCode());
    }

    private void generateReport(String apiUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String executionName = System.getenv().getOrDefault("HEAD_COMMIT_MESSAGE", "local build");
        String executionFrom = System.getenv().getOrDefault("BUILD_URL", "local");
        String executionType = System.getenv().getOrDefault("EXECUTION_TYPE", "local");

        String query = String.format(
                "?project_id=%s&execution_name=%s&execution_from=%s&execution_type=%s",
                PROJECT_ID,
                urlEncode(executionName),
                urlEncode(executionFrom),
                urlEncode(executionType)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/generate-report" + query))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Генерация отчёта: {}", response.statusCode());
    }

    private static String urlEncode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}