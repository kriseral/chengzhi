package dev.chengzhi.qa;

import dev.chengzhi.search.RetrievedChunk;
import java.util.List;

public class ExtractiveAnswerModel implements AnswerModel {

    @Override
    public ModelAnswer answer(String question, List<RetrievedChunk> evidence) {
        if (evidence.isEmpty()) {
            return new ModelAnswer("知识库中没有足够证据回答这个问题。", List.of(), true);
        }
        RetrievedChunk first = evidence.get(0);
        return new ModelAnswer(first.content(), List.of(first.citationId()), false);
    }
}
