package com.zikan.movieAPI.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zikan.movieAPI.dto.MovieDto;
import com.zikan.movieAPI.dto.MoviePageResponse;
import com.zikan.movieAPI.exceptions.EmptyFileException;
import com.zikan.movieAPI.service.MovieService;
import com.zikan.movieAPI.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException, EmptyFileException {

       if (file.isEmpty()){
           throw new EmptyFileException("file is Empty: please send a another file");
       }

         MovieDto dto = convertToMovieDto(movieDto);
         return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHander (@PathVariable Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity <List<MovieDto>> getAllMoviesHandler (){

        return ResponseEntity.ok(movieService.getAllMovies());
    }


    @PutMapping("/update/{movieId}")
    ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                @RequestPart MultipartFile file,
                                                @RequestPart String movieDtoObj) throws IOException{

        if (file.isEmpty()) file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));

    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
}
@GetMapping("/allMoviesPage")
public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
        @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
        @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
){
        return  ResponseEntity.ok(movieService.getAllMovieWithPagination(pageNumber, pageSize));
}

@GetMapping("/allMoviesPageSort")
public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
        @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
        @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
        @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
        @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir
){
        return  ResponseEntity.ok(movieService.getAllMovieWithPaginationAndSorting(pageNumber, pageSize, sortBy, dir));
}


         // covert movie object to movieDto
    // convert a string to a JSON
    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
