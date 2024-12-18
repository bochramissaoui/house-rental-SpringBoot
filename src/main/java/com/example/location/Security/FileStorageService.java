package com.example.location.Security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(byte[] fileBytes, String originalFileName) throws IOException {
        // Generate a unique filename
        String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFileName;
        Path uploadPath = Paths.get(uploadDir);

        // Create directory if it does not exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copy file to target location
        Path targetLocation = uploadPath.resolve(uniqueFileName);
        Files.write(targetLocation, fileBytes);

        return uniqueFileName; // Return the saved file name
    }
}
