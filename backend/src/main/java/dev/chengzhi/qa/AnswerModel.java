package dev.chengzhi.qa;

import dev.chengzhi.search.RetrievedChunk;
import java.util.List;

public interface AnswerModel {

    ModelAnswer answer(String question, List<RetrievedChunk> evidence);
}
