package com.pbmovies.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbmovies.dto.MovieDTO;
import com.pbmovies.exception.EmptyFileException;
import com.pbmovies.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile multipartFile, @RequestPart String movieDTO) throws IOException {

        if(multipartFile.isEmpty())
        {
            throw new EmptyFileException("File is empty Please send another file");
        }
        MovieDTO dto =    convertToMovieDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto,multipartFile), HttpStatus.CREATED);

    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable Integer movieId)
    {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getAllMovies()
    {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Integer movieId, @RequestPart String movieDTOString, @RequestPart MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty())
        {
            multipartFile = null;
        }
        MovieDTO movieDTO = convertToMovieDTO(movieDTOString);

        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDTO, multipartFile));
    }


    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));

    }

    private MovieDTO convertToMovieDTO(String movieDTOString) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        MovieDTO movieDTO = objectMapper.readValue(movieDTOString,MovieDTO.class);
        return movieDTO;
    }

}
