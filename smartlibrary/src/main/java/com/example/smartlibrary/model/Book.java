package com.example.smartlibrary.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;
    private String title;
    private String subtitle;
    private String category;
    private String thumbnail;

    @Column(length = 5000)
    private String description;

    private Integer year;
    private Double averageRating;
    private Integer numberOfPages;
    private Integer numberOfRatings;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

}
