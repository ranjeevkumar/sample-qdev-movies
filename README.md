# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! A swashbuckling movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate's flair!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Search for yer favorite treasures by name, ID, or genre!
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **🏴‍☠️ Pirate-Themed Interface**: Search the seven seas for movies with pirate language and styling!

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🔍 Movie Search**: http://localhost:8080/movies/search (with query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie listing with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form.

### 🔍 Search Movies (NEW!)
```
GET /movies/search
```
Arrr! Search for movies using various criteria, ye savvy pirate!

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Specific movie ID to find (1-12)
- `genre` (optional): Genre to filter by (partial match, case-insensitive)

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Search by specific ID
http://localhost:8080/movies/search?id=5

# Combine multiple criteria
http://localhost:8080/movies/search?name=the&genre=action

# Case-insensitive search
http://localhost:8080/movies/search?name=THE&genre=DRAMA
```

**Response Features:**
- 🏴‍☠️ Pirate-themed search messages
- Handles empty results with encouraging messages
- Preserves search criteria in form fields
- Shows search result count
- Provides "Clear Search" functionality

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🔍 Search Functionality

### Search Features
- **Partial Name Matching**: Search for movies containing specific words
- **Case-Insensitive**: Works with any combination of upper/lower case
- **Genre Filtering**: Find movies by genre (supports partial matches)
- **ID-Based Search**: Find specific movies by their unique ID
- **Multi-Criteria Search**: Combine name, ID, and genre filters
- **Empty Result Handling**: Friendly pirate messages when no movies match
- **Form Persistence**: Search criteria remain in form after search

### Available Genres
The search supports filtering by these genres:
- Action
- Adventure/Fantasy
- Adventure/Sci-Fi
- Comedy
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

### Search Examples

**Find all movies with "The" in the title:**
```
/movies/search?name=the
```

**Find all Drama movies:**
```
/movies/search?genre=drama
```

**Find movies with "War" in title and "Sci-Fi" genre:**
```
/movies/search?name=war&genre=sci-fi
```

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: Search functionality, edge cases, validation
- **MoviesController**: Search endpoint, parameter handling, error cases
- **Integration Tests**: End-to-end search functionality

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that the application started successfully
2. Verify the movies.json file is present in resources
3. Check logs for any errors during startup
4. Ensure proper URL encoding for search parameters

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (director search, year range, rating filters)
- Improve the responsive design
- Add more comprehensive error handling
- Implement pagination for large result sets

### Adding New Movies

To add new movies, edit `src/main/resources/movies.json`:

```json
{
  "id": 13,
  "movieName": "New Adventure",
  "director": "New Director",
  "year": 2024,
  "genre": "Adventure",
  "description": "An exciting new adventure...",
  "duration": 120,
  "imdbRating": 4.5
}
```

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate through this treasure chest of movies! 🏴‍☠️⚓*
