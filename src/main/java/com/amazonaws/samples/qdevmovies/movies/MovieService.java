package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on provided criteria with pirate flair!
     * Arrr! This method be searchin' through our treasure chest of movies, matey!
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searchin' for movies with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If searchin' by ID specifically, try to find that treasure first!
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            if (movieById.isPresent()) {
                Movie movie = movieById.get();
                // Check if this movie also matches other criteria, if provided
                if (matchesSearchCriteria(movie, name, genre)) {
                    results.add(movie);
                    logger.info("Arrr! Found treasure by ID: '{}'", movie.getMovieName());
                }
            }
            return results;
        }
        
        // Search through all movies in our treasure chest
        for (Movie movie : movies) {
            if (matchesSearchCriteria(movie, name, genre)) {
                results.add(movie);
            }
        }
        
        logger.info("Shiver me timbers! Found {} movies matching yer search criteria", results.size());
        return results;
    }

    /**
     * Checks if a movie matches the search criteria
     * 
     * @param movie The movie to check
     * @param name Name criteria (can be null)
     * @param genre Genre criteria (can be null)
     * @return true if movie matches all provided criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String name, String genre) {
        // Check name criteria (partial match, case-insensitive)
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            String movieName = movie.getMovieName().toLowerCase();
            if (!movieName.contains(searchName)) {
                return false;
            }
        }
        
        // Check genre criteria (partial match, case-insensitive)
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            String movieGenre = movie.getGenre().toLowerCase();
            if (!movieGenre.contains(searchGenre)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Gets all unique genres from our movie collection
     * Useful for populating search dropdowns, ye savvy pirate!
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
}
