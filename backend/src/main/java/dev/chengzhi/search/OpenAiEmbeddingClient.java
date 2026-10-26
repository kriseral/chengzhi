package dev.chengzhi.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.model.ModelClientException;
import dev.chengzhi.qa.model.OpenAiEndpoint;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OpenAiEmbeddingClient implements EmbeddingClient {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final AppProperties.Embedding properties;
    private final HttpClient httpClient;

    public OpenAiEmbeddingClient(AppProperties properties) {
        this(properties.embedding(), HttpClient.newHttpClient());
    }

    OpenAiEmbeddingClient(AppProperties.Embedding properties, HttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    @Override
    public float[] embed(String text) {
        return embedAll(List.of(text)).get(0);
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        if (!properties.configured() || texts.isEmpty()) {
            return List.of();
        }
        HttpRequest request = HttpRequest.newBuilder(OpenAiEndpoint.embeddings(properties.baseUrl()))
                .timeout(properties.timeout())
                .header("Authorization", "Bearer " + properties.apiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body(texts), StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ModelClientException("向量服务返回 HTTP " + response.statusCode());
            }
            return read(response.body(), texts.size(), properties.dimensions());
        } catch (HttpTimeoutException exception) {
            throw new ModelClientException("向量请求超时", exception);
        } catch (ModelClientException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelClientException("向量请求被中断", exception);
        } catch (Exception exception) {
            throw new ModelClientException("向量请求失败", exception);
        }
    }

    private String body(List<String> texts) {
        ObjectNode root = JSON.createObjectNode();
        root.put("model", properties.name());
        ArrayNode input = root.putArray("input");
        texts.forEach(input::add);
        return root.toString();
    }

    static List<float[]> read(String body, int expected, int dimensions) throws Exception {
        JsonNode data = JSON.readTree(body).path("data");
        if (!data.isArray() || data.size() != expected) {
            throw new ModelClientException("向量响应数量不正确");
        }
        List<JsonNode> rows = new ArrayList<>();
        data.forEach(rows::add);
        rows.sort(Comparator.comparingInt(row -> row.path("index").asInt()));
        List<float[]> vectors = new ArrayList<>();
        for (JsonNode row : rows) {
            JsonNode embedding = row.path("embedding");
            if (!embedding.isArray() || embedding.size() != dimensions) {
                throw new ModelClientException("向量维度与配置不一致");
            }
            float[] vector = new float[dimensions];
            for (int index = 0; index < dimensions; index++) {
                vector[index] = embedding.get(index).floatValue();
            }
            vectors.add(vector);
        }
        return List.copyOf(vectors);
    }
}
