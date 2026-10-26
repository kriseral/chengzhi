package dev.chengzhi.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WordExtractor {

    public static String extract(Path file) throws IOException {
        // Simple Word parsing - in real use use Apache POI or LibreOffice
        String content = Files.readString(file);
        return content;
    }
}
