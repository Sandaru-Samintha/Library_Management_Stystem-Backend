package com.example.Library_Management_System.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDto {
    private Long bookID;
    private String bookTitle;
    private String bookAuthor;
    private String bookGenre;
    private double bookPrice;
    private boolean bookAvailable;

}
