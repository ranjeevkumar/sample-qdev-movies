package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("allGenres", movieService.getAllGenres());
        model.addAttribute("searchPerformed", false);
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! Search for movies using various criteria
     * This endpoint be acceptin' query parameters for name, id, and genre
     * 
     * @param name Movie name to search for (optional, partial match)
     * @param id Specific movie ID to find (optional)
     * @param genre Genre to filter by (optional, partial match)
     * @param model Spring model for template rendering
     * @return Template name for rendering search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Searchin' for movies with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        try {
            // Validate that at least one search parameter is provided
            if ((name == null || name.trim().isEmpty()) && 
                id == null && 
                (genre == null || genre.trim().isEmpty())) {
                
                logger.warn("Arrr! No search criteria provided, showin' all movies instead");
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("searchPerformed", false);
                model.addAttribute("searchMessage", "Ahoy! Ye need to provide some search criteria, matey!");
                model.addAttribute("allGenres", movieService.getAllGenres());
                return "movies";
            }
            
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Add results and search info to model
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("allGenres", movieService.getAllGenres());
            
            // Add pirate-themed messages based on results
            if (searchResults.isEmpty()) {
                model.addAttribute("searchMessage", 
                    "Shiver me timbers! No movies found matching yer search criteria. " +
                    "Try adjustin' yer search terms, ye savvy pirate!");
                logger.info("No movies found for search criteria");
            } else {
                model.addAttribute("searchMessage", 
                    String.format("Arrr! Found %d treasure%s matching yer search!", 
                                searchResults.size(), 
                                searchResults.size() == 1 ? "" : "s"));
                logger.info("Found {} movies matching search criteria", searchResults.size());
            }
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Blimey! Error occurred during movie search: {}", e.getMessage(), e);
            model.addAttribute("title", "Search Error");
            model.addAttribute("message", 
                "Arrr! Something went wrong while searchin' for movies. " +
                "Please try again, ye landlubber!");
            return "error";
        }
    }
}