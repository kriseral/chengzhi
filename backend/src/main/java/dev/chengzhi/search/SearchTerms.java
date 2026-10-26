package dev.chengzhi.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SearchTerms {

    private static final Pattern TOKEN = Pattern.compile("[0-9A-Za-z]+|[\\u4e00-\\u9fff]");

    private SearchTerms() {
    }

    public static String toQuery(String value) {
        List<String> tokens = tokens(value);
        if (tokens.isEmpty()) {
            return "________";
        }
        return String.join(" | ", tokens);
    }

    private static List<String> tokens(String value) {
        Matcher matcher = TOKEN.matcher(value == null ? "" : value);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group().toLowerCase());
        }
        return tokens;
    }
}
