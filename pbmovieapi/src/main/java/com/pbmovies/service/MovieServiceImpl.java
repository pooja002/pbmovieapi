package com.pbmovies.service;

import com.pbmovies.dto.MovieDTO;
import com.pbmovies.dto.MoviePageResponse;
import com.pbmovies.entities.Movie;
import com.pbmovies.exception.FileExistsException;
import com.pbmovies.exception.MovieNotFoundException;
import com.pbmovies.repository.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;
    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseURL;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {


        // 1. Upload the file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please enter another file name!");
        }

        ;
        String uploadedFile = fileService.uploadFile(path, file);
        // 2. Set the value of field 'poster' as a filename
        movieDTO.setPoster(uploadedFile);
        // 3. Map DTO to Movie Object
        Movie movie = this.modelMapper.map(movieDTO, Movie.class);
        // 4. Save Movie Object
        Movie savedMovie = movieRepository.save(movie);
        // 5. generate posterURL
        String posterURL = baseURL + "/file/" + uploadedFile;
        ///6. Map Movie Object to DTO object and return it
        MovieDTO response = this.modelMapper.map(savedMovie, MovieDTO.class);
        response.setPosterURL(posterURL);
        return response;
    }

    @Override
    public MovieDTO getMovie(Integer movieId) {

        // Check if MovieID exists in the DB, fetch the data
        Optional<Movie> movie = Optional.ofNullable(movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not Found for this id " + movieId)));
        String posterURL = null;
        if (movie != null) {
            posterURL = baseURL + "/file/" + movie.get().getPoster();
        }
        MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
        movieDTO.setPosterURL(posterURL);
        // Generate poster URL
        // Map to Movie DTO Object and return
        return movieDTO;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        // 1. Fetch data from DB
        // 2. Iterate through List
        // 3. Generate Poster URL for each movie object
        // 4. Map to movieDTO Object
        List<MovieDTO> movieDTOList = new ArrayList<>();
        List<Movie> moviesList = movieRepository.findAll();
        moviesList.forEach((movie -> {
            String posterURL = baseURL + "/file/" + movie.getPoster();
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            movieDTO.setPosterURL(posterURL);
            movieDTOList.add(movieDTO);
        }));

        return movieDTOList;
    }

    @Override
    public MovieDTO updateMovie(Integer movieId, MovieDTO movieDTO, MultipartFile multipartFile) throws IOException {
        // 1. Check if Movie object exists with give movie ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new MovieNotFoundException("Movie not found"));
        // 2. If Step1 satisfies, if , file == null, do nothing
        // 3. If Step1 satisfies, if file!=null, delete existing file & upload new file
        String posterURL = null;
        String fileName = movie.getPoster();
        if (multipartFile != null) {

            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, multipartFile);

        }

        posterURL = baseURL + "/file/" + fileName;
        // 4. Set Movie DTO poster Value
        Movie mv = modelMapper.map(movieDTO, Movie.class);
        mv.setPoster(fileName);
        mv = movieRepository.save(mv);
        MovieDTO response = this.modelMapper.map(mv, MovieDTO.class);
        response.setPosterURL(posterURL);
        // 5. Map to Movie, save movie Object and then generate poster URL
        // 6. Map to MovieDTO and return
        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check movie object exists in DB
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found"));
        // 2. Delete the file
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        // 3. Delete movie Object
        movieRepository.deleteById(movie.getId());
        return "Movie is deleted " + movie.getId();
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        List<MovieDTO> movieDTOList = new ArrayList<>();
        movies.forEach((movie -> {
            String posterURL = baseURL + "/file/" + movie.getPoster();
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            movieDTO.setPosterURL(posterURL);
            movieDTOList.add(movieDTO);
        }));
        return new MoviePageResponse(movieDTOList, pageNumber, pageSize, moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();
        List<MovieDTO> movieDTOList = new ArrayList<>();
        movies.forEach((movie -> {
            String posterURL = baseURL + "/file/" + movie.getPoster();
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            movieDTO.setPosterURL(posterURL);
            movieDTOList.add(movieDTO);
        }));

        return new MoviePageResponse(movieDTOList, pageNumber, pageSize, moviePage.getTotalElements(), moviePage.getTotalPages(), moviePage.isLast());
    }
}
