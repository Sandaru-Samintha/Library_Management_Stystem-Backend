package com.example.Library_Management_System.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${book.image.path}")
    private String bookImagePath;

    @Value("${member.image.path}")
    private String memberImagePath;

    @Value("${admin.image.path}")
    private String adminImagePath;

    public String saveBookImage(MultipartFile file) throws IOException {
        return saveImage(file, bookImagePath);
    }

    public String saveMemberImage(MultipartFile file) throws IOException {
        return saveImage(file, memberImagePath);
    }

    public String saveAdminImage(MultipartFile file) throws IOException {
        return saveImage(file, adminImagePath);
    }

    private String saveImage(MultipartFile file, String directory) throws IOException {
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return URL path for accessing the image
        return "/images/" + (directory.contains("books") ? "books/" :
                directory.contains("members") ? "members/" : "admins/") + fileName;
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            String directory;

            if (imageUrl.contains("/books/")) {
                directory = bookImagePath;
            } else if (imageUrl.contains("/members/")) {
                directory = memberImagePath;
            } else {
                directory = adminImagePath;
            }

            Path filePath = Paths.get(directory + fileName);
            Files.deleteIfExists(filePath);
        }
    }
}