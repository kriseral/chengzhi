package dev.chengzhi.ingest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PDFExtractor {

    public static String extract(Path file) throws IOException {
        String content = extractWithTika(file);
        if (content != null && !content.isBlank()) {
            return content;
        }
        return extractWithPDFBox(file);
    }

    private static String extractWithTika(Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            var handler = new org.apache.tika.sax.BodyContentHandler();
            var metadata = new Metadata();
            var context = new ParseContext();
            new PDFParser().parse(is, handler, metadata, context);
            return handler.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractWithPDFBox(Path file) throws IOException {
        try (var document = PDDocument.load(file.toFile())) {
            var stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
