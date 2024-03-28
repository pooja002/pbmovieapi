package com.pbmovies.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Integer id;
    @NotBlank(message = "Please provide movie's release Year")
    private Integer releaseYear;

    @NotBlank(message = "Please provide movie's title")
    private String title;

    @NotBlank(message = "Please provide movie's director")
    private String director;

    @NotBlank(message = "Please provide movie's studio")
    private String studio;

    private Set<String> movieCast;
    @NotBlank(message = "Please provide movie's poster")
    private String poster;
    @NotBlank(message = "Please provide movie's poster URL")
    private  String posterURL;
}
