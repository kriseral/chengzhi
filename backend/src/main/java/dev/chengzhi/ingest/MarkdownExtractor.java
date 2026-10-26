package dev.chengzhi.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MarkdownExtractor {

    public static String extract(Path file) throws IOException {
        String content = Files.readString(file);
        return content;
    }
}
