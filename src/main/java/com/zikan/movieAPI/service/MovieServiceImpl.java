package com.zikan.movieAPI.service;

import com.zikan.movieAPI.Repository.MovieRepository;
import com.zikan.movieAPI.dto.MovieDto;
import com.zikan.movieAPI.dto.MoviePageResponse;
import com.zikan.movieAPI.entities.Movie;
import com.zikan.movieAPI.exceptions.FileExistsException;
import com.zikan.movieAPI.exceptions.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.events.Event;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final FileService fileService;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;
    @Value("${base.url}")
    private String baseUrl;


    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        // upload d file (alsocheck if d file already exists in  db)
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists Please enter another file name");

        }
        String uploadedFileName = fileService.uploadFile(path, file);

        //  set the value of field poster as filename

        movieDto.setPoster(uploadedFileName);

        // map dto to movie object
        Movie movie = new Movie(
//                movieDto.getMovieId(),
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // save d movie object

        Movie savedMovie = movieRepository.save(movie);
        // 5 generate the postal url

        String posterUrl = baseUrl + "/api/file/" + uploadedFileName;

        // map movie object to dto objet and return it
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl

        );

        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {


        // 1. To get movie by ID we do d floowing
        //1. Check d data in db and if exists, fetch d data of given ID

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie Not found with id = " + movieId));

        // 2. Generate posterUrl
        String posterUrl = baseUrl + "/api/file/" + movie.getPoster();

        // 3. Map to MovieDTO object and return it

        MovieDto response = new MovieDto(

                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {

        // fetch all data from DB

        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        //Iterate through the list, generate posterUrl for each movie obj and map to movieDto db

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/api/file/" + movie.getPoster();

            MovieDto movieDto = new MovieDto(

                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl

            );
            movieDtos.add(movieDto);


        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {

        //1 1.  CHECK IF MOVIE OBJECT EXISTS WITH given movieId

        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie Not found with id " + movieId));


        // if file is null, do nothing
        // if file is not null, Then delete existing file associated with the record
        // and upload the new file
        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }


        // 3. set movieDto'd value, accordign to step 3
        movieDto.setPoster(fileName);

        // 4. map it to movie object
        Movie movie = new Movie(

                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPosterUrl()

        );

        //5 save the movie object and return it

        Movie updatedNovie = movieRepository.save(movie);

        //generate postalUrl for it
        String posterUrl = baseUrl + "/api/file/" + fileName;

        //7 map to MovieDto and return it

        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );


        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {

        // first iss to chck if movie object exists in the DB
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found with id " + movieId));
        Integer id = mv.getMovieId();

        //2 Delete the file associated with this object
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        //3 delete the movie object
        movieRepository.delete(mv);

        return "Movie with Id Deleted Successfully" + id;
    }

    @Override
    public MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        //to get list of all the movies

        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/api/file/" + movie.getPoster();

            MovieDto movieDto = new MovieDto(

                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl

            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalPages(),
                moviePages.getTotalElements(),
                moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        //to get list of all the movies

        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/api/file/" + movie.getPoster();

            MovieDto movieDto = new MovieDto(

                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl

            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                                    moviePages.getTotalElements(),
                                    moviePages.getTotalPages(),
                                    moviePages.isLast());
    }

}
