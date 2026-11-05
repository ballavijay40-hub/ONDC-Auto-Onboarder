package com.ondc.util;

import com.ondc.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public final class FileUploadUtil {

    private FileUploadUtil() {}

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".pdf", ".png", ".jpg", ".jpeg", ".webp");
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(".png", ".jpg", ".jpeg", ".webp");

    public static String saveFile(String baseDir, String subDir, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "file";
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Unsupported file type: " + extension + ". Allowed: PDF, PNG, JPG, JPEG, WEBP");
        }

        try {
            Path targetDir = Paths.get(baseDir, subDir).toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String uniqueName = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(uniqueName).normalize();

            // Prevent path traversal
            if (!targetPath.startsWith(targetDir)) {
                throw new BadRequestException("Invalid path traversal sequence");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Saved uploaded file to: {}", targetPath);
            return Paths.get(baseDir, subDir, uniqueName).toString().replace('\\', '/');
        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new RuntimeException("Could not store file: " + ex.getMessage(), ex);
        }
    }

    public static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    public static boolean isPdf(String pathOrFilename) {
        return pathOrFilename != null && pathOrFilename.toLowerCase().endsWith(".pdf");
    }

    public static boolean isImage(String pathOrFilename) {
        if (pathOrFilename == null) return false;
        String lower = pathOrFilename.toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }
}

