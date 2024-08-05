package com.zikan.movieAPI.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer movieId;

    @NotBlank(message = "please provide movies's titles")
    private String title;

    @NotBlank (message = "please provide movies's director")
    private String director;

    @NotBlank (message = "please provide movies's studio")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;
    @NotBlank(message = "please provide movies poster!")
    private String poster;
    @NotBlank(message = "please provide poster's url")
    private String posterUrl;

}
