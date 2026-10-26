package dev.chengzhi.qa.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OpenAiChatClient implements ChatClient {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final HttpClient httpClient;

    public OpenAiChatClient() {
        this(HttpClient.newHttpClient());
    }

    OpenAiChatClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ChatResult complete(ChatCommand command) {
        HttpRequest request = HttpRequest.newBuilder(OpenAiEndpoint.chatCompletions(command.baseUrl()))
                .timeout(command.timeout())
                .header("Authorization", "Bearer " + command.apiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body(command), StandardCharsets.UTF_8))
                .build();
        ModelClientException lastError = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return OpenAiResponseReader.read(response.body());
                }
                lastError = new ModelClientException("模型服务返回 HTTP " + response.statusCode());
                if (!retryable(response.statusCode()) || attempt == 2) {
                    throw lastError;
                }
            } catch (HttpTimeoutException exception) {
                throw new ModelClientException("模型请求超时", exception);
            } catch (ModelClientException exception) {
                throw exception;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new ModelClientException("模型请求被中断", exception);
            } catch (Exception exception) {
                lastError = new ModelClientException("模型请求失败", exception);
                if (attempt == 2) {
                    throw lastError;
                }
            }
        }
        throw lastError;
    }

    private static boolean retryable(int status) {
        return status == 429 || status == 502 || status == 503;
    }

    private static String body(ChatCommand command) {
        ObjectNode root = JSON.createObjectNode();
        root.put("model", command.model());
        root.put("temperature", 0);
        root.put("max_tokens", 800);
        ArrayNode messages = root.putArray("messages");
        messages.addObject().put("role", "system").put("content", command.systemPrompt());
        messages.addObject().put("role", "user").put("content", command.userPrompt());
        return root.toString();
    }
}
