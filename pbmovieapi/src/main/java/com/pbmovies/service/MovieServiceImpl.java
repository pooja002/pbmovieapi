package com.pbmovies.service;

import com.pbmovies.dto.MovieDTO;
import com.pbmovies.entities.Movie;
import com.pbmovies.repository.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl  implements MovieService{


    private final MovieRepository movieRepository;

    private  final FileService fileService;


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
       String uploadedFile = fileService.uploadFile(path,file);
       // 2. Set the value of field 'poster' as a filename
       movieDTO.setPoster(uploadedFile);
        // 3. Map DTO to Movie Object
        Movie movie = this.modelMapper.map(movieDTO,Movie.class);
        // 4. Save Movie Object
        Movie savedMovie = movieRepository.save(movie);
        // 5. generate posterURL
        String posterURL = baseURL + "/file/" + uploadedFile;
        ///6. Map Movie Object to DTO object and return it
        MovieDTO response = this.modelMapper.map(savedMovie,MovieDTO.class);
        response.setPosterURL(posterURL);
        return response;
    }

    @Override
    public MovieDTO getMovie(Integer movieId) {

        // Check if MovieID exists in the DB, fetch the data
       Optional<Movie> movie = Optional.ofNullable(movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie Not Found")));
        String posterURL = null;
       if(movie!=null)
       {
           posterURL  = baseURL + "/file/" + movie.get().getPoster();
       }
       MovieDTO movieDTO = modelMapper.map(movie,MovieDTO.class);
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
            String posterURL  = baseURL + "/file/" + movie.getPoster();
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            movieDTO.setPosterURL(posterURL);
            movieDTOList.add(movieDTO);
        }));

        return movieDTOList;
    }
}
