package com.pbmovies.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's release Year")
    private Integer releaseYear;
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's title")
    private String title;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's director")
    private String director;
    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's studio")
    private String studio;
    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;
    private String poster;

}
