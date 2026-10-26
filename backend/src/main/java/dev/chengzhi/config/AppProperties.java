package dev.chengzhi.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chengzhi")
public record AppProperties(Auth auth, Ingest ingest, Qa qa, Embedding embedding) {

    public record Auth(Duration tokenTtl) {
    }

    public record Ingest(int chunkSize, int chunkOverlap) {
    }

    public record Qa(int topK, Model model) {
    }

    public record Model(
            boolean enabled,
            String baseUrl,
            String apiKey,
            String name,
            Duration timeout,
            String promptVersion) {

        public boolean configured() {
            return enabled && present(baseUrl) && present(apiKey) && present(name);
        }

        private static boolean present(String value) {
            return value != null && !value.isBlank();
        }
    }

    public record Approval(String policy) {
    }
