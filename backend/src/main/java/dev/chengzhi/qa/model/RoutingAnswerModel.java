package dev.chengzhi.qa.model;

import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.AnswerModel;
import dev.chengzhi.qa.ExtractiveAnswerModel;
import dev.chengzhi.qa.ModelAnswer;
import dev.chengzhi.search.RetrievedChunk;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RoutingAnswerModel implements AnswerModel {

    static final String UNAVAILABLE = "模型暂时无法生成可验证回答。";

    private static final Logger log = LoggerFactory.getLogger(RoutingAnswerModel.class);

    private final AppProperties properties;
    private final ExtractiveAnswerModel fallback;
    private final OpenAiCompatibleAnswerModel remote;

    @Autowired
    public RoutingAnswerModel(AppProperties properties) {
        this(properties, new ExtractiveAnswerModel(), new OpenAiCompatibleAnswerModel(properties.qa().model(), new OpenAiChatClient()));
    }

    RoutingAnswerModel(AppProperties properties, ExtractiveAnswerModel fallback, OpenAiCompatibleAnswerModel remote) {
        this.properties = properties;
        this.fallback = fallback;
        this.remote = remote;
    }

    @Override
    public ModelAnswer answer(String question, List<RetrievedChunk> evidence) {
        if (!properties.qa().model().configured()) {
            log.info("model call skipped outcome=fallback promptVersion={}", properties.qa().model().promptVersion());
            return fallback.answer(question, evidence);
        }
        long startedAt = System.nanoTime();
        try {
            ModelAnswer answer = remote.answer(question, evidence);
            log.info(
                    "model call model={} promptVersion={} elapsedMs={} outcome={}",
                    properties.qa().model().name(),
                    properties.qa().model().promptVersion(),
                    elapsed(startedAt),
                    answer.refused() ? "refused" : "answered");
            return answer;
        } catch (RuntimeException exception) {
            log.warn(
                    "model call model={} promptVersion={} elapsedMs={} outcome=failed reason={}",
                    properties.qa().model().name(),
                    properties.qa().model().promptVersion(),
                    elapsed(startedAt),
                    exception.getMessage());
            return new ModelAnswer(UNAVAILABLE, List.of(), true);
        }
    }

    private static long elapsed(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
