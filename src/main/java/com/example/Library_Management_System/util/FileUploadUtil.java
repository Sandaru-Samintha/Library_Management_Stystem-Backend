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

    @Value("${book.image.path:src/main/resources/static/images/books/}")
    private String bookImagePath;

    @Value("${member.image.path:src/main/resources/static/images/members/}")
    private String memberImagePath;

    @Value("${admin.image.path:src/main/resources/static/images/admins/}")
    private String adminImagePath;

    public String saveBookImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return saveImage(file, bookImagePath, "books");
    }

    public String saveMemberImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return saveImage(file, memberImagePath, "members");
    }

    public String saveAdminImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return saveImage(file, adminImagePath, "admins");
    }

    private String saveImage(MultipartFile file, String directory, String folderName) throws IOException {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created directory: " + uploadPath.toAbsolutePath());
            }

            // Validate file
            if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                throw new IOException("File name is empty");
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFileName);

            // Handle case when extension is null or empty
            if (extension == null || extension.isEmpty()) {
                extension = "jpg"; // default extension
            }

            String fileName = UUID.randomUUID().toString() + "." + extension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File saved successfully: " + filePath.toAbsolutePath());

            // Return URL path for accessing the image
            return "/images/" + folderName + "/" + fileName;

        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw e;
        }
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            String directory;

            if (imageUrl.contains("/books/")) {
                directory = bookImagePath;
            } else if (imageUrl.contains("/members/")) {
                directory = memberImagePath;
            } else if (imageUrl.contains("/admins/")) {
                directory = adminImagePath;
            } else {
                System.err.println("Unknown image type: " + imageUrl);
                return;
            }

            Path filePath = Paths.get(directory + fileName);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                System.out.println("Deleted image: " + filePath);
            } else {
                System.out.println("Image not found: " + filePath);
            }

        } catch (Exception e) {
            System.err.println("Error deleting image: " + e.getMessage());
            throw e;
        }
    }
}