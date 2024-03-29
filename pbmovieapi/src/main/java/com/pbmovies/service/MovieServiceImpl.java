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
import java.util.List;

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
        return null;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        return null;
    }
}
