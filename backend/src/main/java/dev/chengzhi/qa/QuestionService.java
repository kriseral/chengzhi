package dev.chengzhi.qa;

import dev.chengzhi.acl.AccessScope;
import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.CitationGuard.GuardResult;
import dev.chengzhi.search.RetrievedChunk;
import dev.chengzhi.search.SearchService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final SearchService search;
    private final AnswerModel model;
    private final AppProperties properties;

    public QuestionService(SearchService search, AnswerModel model, AppProperties properties) {
        this.search = search;
        this.model = model;
        this.properties = properties;
    }

    public AskResponse ask(AccessScope scope, String question) {
        List<RetrievedChunk> evidence = search.search(scope, question, properties.qa().topK());
        ModelAnswer draft = model.answer(question, evidence);
        GuardResult checked = CitationGuard.check(draft, evidence);
        List<RetrievedChunk> cited = evidence.stream()
                .filter(chunk -> checked.citationIds().contains(chunk.citationId()))
                .toList();
        return new AskResponse(checked.answer(), checked.refused(), checked.citationIds(), cited);
    }
}
