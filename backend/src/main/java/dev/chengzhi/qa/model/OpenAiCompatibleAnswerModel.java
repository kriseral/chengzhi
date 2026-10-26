package dev.chengzhi.qa.model;

import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.AnswerModel;
import dev.chengzhi.qa.ModelAnswer;
import dev.chengzhi.search.RetrievedChunk;
import java.util.List;

public class OpenAiCompatibleAnswerModel implements AnswerModel {

    private final AppProperties.Model settings;
    private final ChatClient client;

    public OpenAiCompatibleAnswerModel(AppProperties.Model settings, ChatClient client) {
        this.settings = settings;
        this.client = client;
    }

    @Override
    public ModelAnswer answer(String question, List<RetrievedChunk> evidence) {
        ChatResult result = client.complete(new ChatCommand(
                settings.baseUrl(),
                settings.apiKey(),
                settings.name(),
                EvidencePrompt.SYSTEM_PROMPT,
                EvidencePrompt.userPrompt(question, evidence),
                settings.timeout()));
        return ModelAnswerParser.parse(result.content());
    }
}
