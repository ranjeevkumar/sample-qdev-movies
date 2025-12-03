package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Unit tests for the MovieService search functionality
 * These tests be checkin' that our treasure huntin' methods work properly!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Arrr! Test searchin' movies by name - partial match")
    public void testSearchMoviesByName() {
        // Search for movies containing "The" in the name
        List<Movie> results = movieService.searchMovies("The", null, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0, "Should find movies with 'The' in the name");
        
        // Verify all results contain "The" in the name (case-insensitive)
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"), 
                      "Movie name should contain 'the': " + movie.getMovieName());
        }
    }

    @Test
    @DisplayName("Test searchin' movies by name - case insensitive")
    public void testSearchMoviesByNameCaseInsensitive() {
        // Search with different cases
        List<Movie> resultsLower = movieService.searchMovies("prison", null, null);
        List<Movie> resultsUpper = movieService.searchMovies("PRISON", null, null);
        List<Movie> resultsMixed = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(resultsLower);
        assertNotNull(resultsUpper);
        assertNotNull(resultsMixed);
        
        // All should return the same results
        assertEquals(resultsLower.size(), resultsUpper.size());
        assertEquals(resultsLower.size(), resultsMixed.size());
        
        if (!resultsLower.isEmpty()) {
            assertEquals(resultsLower.get(0).getId(), resultsUpper.get(0).getId());
            assertEquals(resultsLower.get(0).getId(), resultsMixed.get(0).getId());
        }
    }

    @Test
    @DisplayName("Shiver me timbers! Test searchin' by specific ID")
    public void testSearchMoviesById() {
        // Search for movie with ID 1
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertNotNull(results);
        assertEquals(1, results.size(), "Should find exactly one movie with ID 1");
        assertEquals(1L, results.get(0).getId(), "Found movie should have ID 1");
    }

    @Test
    @DisplayName("Test searchin' by ID with additional criteria")
    public void testSearchMoviesByIdWithAdditionalCriteria() {
        // First, get a movie to test with
        Optional<Movie> testMovie = movieService.getMovieById(1L);
        assertTrue(testMovie.isPresent(), "Test movie should exist");
        
        Movie movie = testMovie.get();
        
        // Search by ID with matching genre - should find the movie
        List<Movie> matchingResults = movieService.searchMovies(null, 1L, movie.getGenre());
        assertEquals(1, matchingResults.size(), "Should find movie when ID and genre match");
        
        // Search by ID with non-matching genre - should find no movies
        List<Movie> nonMatchingResults = movieService.searchMovies(null, 1L, "NonExistentGenre");
        assertEquals(0, nonMatchingResults.size(), "Should find no movies when ID exists but genre doesn't match");
    }

    @Test
    @DisplayName("Arrr! Test searchin' by genre")
    public void testSearchMoviesByGenre() {
        // Search for Drama movies
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        
        assertNotNull(results);
        assertTrue(results.size() > 0, "Should find Drama movies");
        
        // Verify all results contain "Drama" in the genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                      "Movie genre should contain 'drama': " + movie.getGenre());
        }
    }

    @Test
    @DisplayName("Test searchin' by genre - case insensitive")
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> resultsLower = movieService.searchMovies(null, null, "drama");
        List<Movie> resultsUpper = movieService.searchMovies(null, null, "DRAMA");
        List<Movie> resultsMixed = movieService.searchMovies(null, null, "Drama");
        
        assertNotNull(resultsLower);
        assertNotNull(resultsUpper);
        assertNotNull(resultsMixed);
        
        // All should return the same number of results
        assertEquals(resultsLower.size(), resultsUpper.size());
        assertEquals(resultsLower.size(), resultsMixed.size());
    }

    @Test
    @DisplayName("Test searchin' with multiple criteria")
    public void testSearchMoviesWithMultipleCriteria() {
        // Search for movies with "The" in name and "Drama" in genre
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        
        assertNotNull(results);
        
        // Verify all results match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"), 
                      "Movie name should contain 'the': " + movie.getMovieName());
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                      "Movie genre should contain 'drama': " + movie.getGenre());
        }
    }

    @Test
    @DisplayName("Blimey! Test searchin' with no criteria returns empty")
    public void testSearchMoviesWithNoCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list when no criteria provided");
    }

    @Test
    @DisplayName("Test searchin' with empty strings")
    public void testSearchMoviesWithEmptyStrings() {
        List<Movie> results = movieService.searchMovies("", null, "");
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list when empty strings provided");
    }

    @Test
    @DisplayName("Test searchin' with whitespace-only strings")
    public void testSearchMoviesWithWhitespaceStrings() {
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list when whitespace-only strings provided");
    }

    @Test
    @DisplayName("Test searchin' for non-existent movie")
    public void testSearchMoviesNonExistent() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        
        assertNotNull(results);
        assertEquals(0, results.size(), "Should return empty list for non-existent movie");
    }

    @Test
    @DisplayName("Test searchin' by invalid ID")
    public void testSearchMoviesByInvalidId() {
        // Test with negative ID
        List<Movie> negativeResults = movieService.searchMovies(null, -1L, null);
        assertEquals(0, negativeResults.size(), "Should return empty list for negative ID");
        
        // Test with zero ID
        List<Movie> zeroResults = movieService.searchMovies(null, 0L, null);
        assertEquals(0, zeroResults.size(), "Should return empty list for zero ID");
        
        // Test with very large ID
        List<Movie> largeResults = movieService.searchMovies(null, 999999L, null);
        assertEquals(0, largeResults.size(), "Should return empty list for non-existent large ID");
    }

    @Test
    @DisplayName("Arrr! Test getAllGenres method")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres);
        assertTrue(genres.size() > 0, "Should return at least one genre");
        
        // Check that genres are unique (no duplicates)
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount, "All genres should be unique");
        
        // Check that genres are sorted
        List<String> sortedGenres = genres.stream().sorted().collect(java.util.stream.Collectors.toList());
        assertEquals(sortedGenres, genres, "Genres should be sorted alphabetically");
    }

    @Test
    @DisplayName("Test that search preserves original movie data")
    public void testSearchPreservesMovieData() {
        List<Movie> allMovies = movieService.getAllMovies();
        List<Movie> searchResults = movieService.searchMovies("The", null, null);
        
        // Verify that search results contain complete movie data
        for (Movie movie : searchResults) {
            assertNotNull(movie.getMovieName());
            assertNotNull(movie.getDirector());
            assertNotNull(movie.getGenre());
            assertNotNull(movie.getDescription());
            assertTrue(movie.getId() > 0);
            assertTrue(movie.getYear() > 0);
            assertTrue(movie.getDuration() > 0);
            assertTrue(movie.getImdbRating() >= 0);
        }
    }
}