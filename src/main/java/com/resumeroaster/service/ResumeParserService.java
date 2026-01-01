package com.resumeroaster.service;

import com.resumeroaster.exception.ParsingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Service for parsing resume files (PDF/Docx) and extracting text content.
 */
@Service
@Slf4j
public class ResumeParserService {

    /**
     * Parse uploaded resume file and extract text content.
     * @param filePath path to the uploaded file
     * @return extracted text content
     * @throws ParsingException if file cannot be parsed
     */
    public String parseResume(Path filePath) {
        String filename = filePath.getFileName().toString().toLowerCase();

        try {
            String text;
            if (filename.endsWith(".pdf")) {
                text = parsePdf(filePath);
            } else if (filename.endsWith(".docx")) {
                text = parseDocx(filePath);
            } else if (filename.endsWith(".txt")) {
                text = parseTxt(filePath);
            } else {
                throw new ParsingException("Unsupported file format: " + filename);
            }

            if (text == null || text.trim().isEmpty()) {
                throw new ParsingException("No readable text found in the resume");
            }

            log.info("Parsed resume: {} characters extracted", text.length());
            return text.trim();

        } catch (IOException e) {
            throw new ParsingException("Failed to parse resume file: " + e.getMessage(), e);
        }
    }

    /**
     * Parse PDF file using Apache PDFBox.
     */
    private String parsePdf(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(is))) {

            if (document.isEncrypted()) {
                throw new ParsingException("Cannot parse encrypted PDF");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Parse DOCX file using Apache POI.
     */
    private String parseDocx(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(is)) {

            return document.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .filter(text -> text != null && !text.isEmpty())
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * Parse TXT file (for testing purposes).
     */
    private String parseTxt(Path filePath) throws IOException {
        return Files.readString(filePath);
    }
}
