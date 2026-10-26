package dev.chengzhi.search;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SearchTermsTest {

    @Test
    void splitsChineseCharactersAndKeepsNumbersAndWords() {
        assertThat(SearchTerms.toQuery("进入实验室前要做哪些准备"))
                .isEqualTo("进 | 入 | 实 | 验 | 室 | 前 | 要 | 做 | 哪 | 些 | 准 | 备");
        assertThat(SearchTerms.toQuery("Room 12A 安全"))
                .isEqualTo("room | 12a | 安 | 全");
    }

    @Test
    void usesImpossibleTermWhenQueryHasNoSearchableText() {
        assertThat(SearchTerms.toQuery("？")).isEqualTo("________");
    }
}
