package com.ondc.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

@Slf4j
public class OCRUtil {

    private final String dataPath;

    public OCRUtil(String dataPath) {
        this.dataPath = dataPath != null && !dataPath.isBlank() ? dataPath : "tessdata";
    }

    public String extractTextFromFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File to OCR does not exist: " + (file != null ? file.getPath() : "null"));
        }

        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else {
            return extractTextFromImage(file);
        }
    }

    public String extractTextFromPdf(File pdfFile) {
        StringBuilder extracted = new StringBuilder();

        // 1. First attempt native text extraction using PDFBox PDFTextStripper
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String directText = stripper.getText(document);
            if (directText != null && directText.trim().length() > 50) {
                log.info("Successfully extracted embedded text from PDF using PDFTextStripper ({} chars)", directText.length());
                return directText.trim();
            }

            // 2. If PDF is scanned or rasterized, render first page as image and run Tesseract OCR
            log.info("PDF has little/no embedded text. Rendering page 1 as high-res image for OCR...");
            PDFRenderer renderer = new PDFRenderer(document);
            int pagesToRender = Math.min(document.getNumberOfPages(), 2);
            for (int page = 0; page < pagesToRender; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String ocrResult = doTesseractOCR(image);
                if (ocrResult != null && !ocrResult.isBlank()) {
                    extracted.append(ocrResult).append("\n");
                }
            }
        } catch (IOException e) {
            log.warn("Failed to load PDF with PDFBox: {}", e.getMessage());
        }

        return extracted.toString().trim();
    }

    public String extractTextFromImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                log.warn("ImageIO returned null for file: {}", imageFile.getAbsolutePath());
                return "";
            }
            return doTesseractOCR(image);
        } catch (IOException e) {
            log.error("Failed to read image file: {}", imageFile.getAbsolutePath(), e);
            return "";
        }
    }

    private String doTesseractOCR(BufferedImage image) {
        try {
            ITesseract tesseract = new Tesseract();
            Path path = Paths.get(dataPath).toAbsolutePath();
            if (Files.exists(path)) {
                tesseract.setDatapath(path.toString());
            }
            tesseract.setLanguage("eng");
            return tesseract.doOCR(image);
        } catch (TesseractException | UnsatisfiedLinkError | NoClassDefFoundError ex) {
            log.warn("Tesseract OCR native invocation failed or tessdata missing ({}), returning empty string: {}",
                    ex.getClass().getSimpleName(), ex.getMessage());
            return "";
        }
    }
}

