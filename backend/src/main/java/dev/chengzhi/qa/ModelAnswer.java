package dev.chengzhi.qa;

import java.util.List;

public record ModelAnswer(String answer, List<String> citationIds, boolean refused) {
}
