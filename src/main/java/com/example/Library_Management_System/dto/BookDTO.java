package com.example.Library_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDTO {

    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookGenre;
    private String bookIsbn;
    private String bookPublisher;
    private Integer bookPublicationYear;
    private Double bookPrice;
    private String bookDescription;
    private String shelfLocation;
    private Integer totalCopies;
    private Integer availableCopies;
    private Boolean bookAvailable;
    private String bookImageUrl;
    private MultipartFile bookImage;
}
