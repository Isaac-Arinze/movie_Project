package com.zikan.movieAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;
    @Column(nullable = false, length = 200)
    @NotBlank (message = "please provide movies's title")
    private String title;
    @Column(nullable = false, length = 200)
    @NotBlank (message = "please provide movies's director")
    private String director;
    @Column(nullable = false, length = 200)
    @NotBlank (message = "please provide movies's studio")
    private String studio;
    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;
    @Column(nullable = false)
    @NotNull(message = "please provide movies release Year!")
    private Integer releaseYear;
    @Column(nullable = false)
    @NotBlank(message = "please provide movies poster!")
    private String poster;

}
