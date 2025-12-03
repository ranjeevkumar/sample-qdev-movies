package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ahoy matey! Unit tests for the MoviesController including search functionality
 * These tests be checkin' that our controller handles requests like a proper pirate!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with test data
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "The Adventure", "Adventure Director", 2022, "Action", "Adventure description", 110, 4.0),
                    new Movie(3L, "Comedy Gold", "Comedy Director", 2021, "Comedy", "Funny description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "The Adventure", "Adventure Director", 2022, "Action", "Adventure description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase())) {
                            matches = false;
                        }
                    }
                    
                    if (id != null && id > 0) {
                        if (!movie.getId().equals(id)) {
                            matches = false;
                        }
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                            matches = false;
                        }
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Test getMovies endpoint")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("allGenres"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(false, model.getAttribute("searchPerformed"));
    }

    @Test
    @DisplayName("Test getMovieDetails endpoint")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Test getMovieDetails with non-existent movie")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    @DisplayName("Arrr! Test searchMovies with name parameter")
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Test", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("searchMessage"));
        assertTrue(model.containsAttribute("allGenres"));
        
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Test", model.getAttribute("searchName"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Test searchMovies with ID parameter")
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals(2L, model.getAttribute("searchId"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(2L, movies.get(0).getId());
    }

    @Test
    @DisplayName("Test searchMovies with genre parameter")
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Drama", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(true, model.getAttribute("searchPerformed"));
        assertEquals("Drama", model.getAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Drama", movies.get(0).getGenre());
    }

    @Test
    @DisplayName("Shiver me timbers! Test searchMovies with no results")
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistentMovie", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals(true, model.getAttribute("searchPerformed"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(0, movies.size());
        
        // Check for pirate-themed no results message
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertNotNull(searchMessage);
        assertTrue(searchMessage.contains("Shiver me timbers"));
        assertTrue(searchMessage.contains("No movies found"));
    }

    @Test
    @DisplayName("Test movieService integration")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        List<String> genres = mockMovieService.getAllGenres();
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Comedy"));
    }
}
