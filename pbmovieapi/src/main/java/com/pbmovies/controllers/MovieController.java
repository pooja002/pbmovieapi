package com.pbmovies.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbmovies.dto.MovieDTO;
import com.pbmovies.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile multipartFile, @RequestPart String movieDTO) throws IOException {
        MovieDTO dto =    convertToMovieDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto,multipartFile), HttpStatus.CREATED);

    }

    private MovieDTO convertToMovieDTO(String movieDTOString) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        MovieDTO movieDTO = objectMapper.readValue(movieDTOString,MovieDTO.class);
        return movieDTO;
    }

}
