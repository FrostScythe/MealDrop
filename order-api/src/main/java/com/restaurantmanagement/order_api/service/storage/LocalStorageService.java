package com.restaurantmanagement.order_api.service.storage;


import com.restaurantmanagement.order_api.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Primary  // ← Spring picks this one while developing locally
public class LocalStorageService implements StorageService {

    // Reads from application.yaml — defaults to "uploads" folder in project root
    @Value("${storage.local.base-path:uploads}")
    private String basePath;

    @Value("${storage.local.base-url:http://localhost:8080/images}")
    private String baseUrl;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp"
    );

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        try {
            // Create folder if it doesn't exist: uploads/menu-items/
            Path uploadDir = Paths.get(basePath, folder);
            Files.createDirectories(uploadDir);

            // Generate unique filename to avoid collisions
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + "." + extension;

            // Save file to disk
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.write(filePath, file.getBytes());

            // Return accessible URL: http://localhost:8080/images/menu-items/uuid.jpg
            return baseUrl + "/" + folder + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            // Convert URL back to file path
            // e.g. http://localhost:8080/images/menu-items/uuid.jpg
            //   -> uploads/menu-items/uuid.jpg
            String relativePath = fileUrl.replace(baseUrl, "");
            Path filePath = Paths.get(basePath + relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't throw — deletion failure shouldn't break the request
            System.err.println("Warning: Could not delete file: " + fileUrl);
        }
    }

    // ─── Helpers ───────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BadRequestException("Image file cannot be empty");

        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new BadRequestException(
                    "Invalid file type. Allowed: JPEG, PNG, WEBP");

        if (file.getSize() > MAX_SIZE_BYTES)
            throw new BadRequestException(
                    "File too large. Maximum size is 5MB");
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return "jpg"; // safe default
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}