package vinicius.cornieri.lets.code.challenge;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.service.csv.CSVFactory;

import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FilterTSVRatings {

    public static void main(String[] args) {
        Instant start = Instant.now();
        HashMap<String, Movie> movies = parseMoviesTitlesTsv();

        processRatingsTSV(movies);

        writeMoviesWithRatingsCSV(movies);

        log.info("Finished in {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    @SneakyThrows
    private static void writeMoviesWithRatingsCSV(HashMap<String, Movie> movies) {
        Path directoryPath = Paths.get(ClassLoader.getSystemResource("imdb.data").toURI()).toAbsolutePath();
        Path titleBasicsWithRatingCsvPath = Path.of(directoryPath.toString(), "title.basics.with.ratings.csv");
        if (!Files.exists(titleBasicsWithRatingCsvPath)) {
            Files.createFile(titleBasicsWithRatingCsvPath);
        }
        log.info("Writing new csv {} from movies HashMap", titleBasicsWithRatingCsvPath);

        Writer writer = new FileWriter(titleBasicsWithRatingCsvPath.toString());

        StatefulBeanToCsv<Movie> sbc = new StatefulBeanToCsvBuilder<Movie>(writer)
            .withSeparator(CSVFactory.CSV_SEPARATOR)
            .build();

        List<Movie> values = movies.values().stream()
            .filter(m -> m.getRating() > 0)
            .collect(Collectors.toList());
        sbc.write(new ArrayList<>(values));
        writer.close();
    }

    @SneakyThrows
    private static void processRatingsTSV(HashMap<String, Movie> movies) {
        String ratingsTsvPath = "imdb.data/title.ratings.tsv";
        log.info("Processing {} filling rating of movies HashMap", ratingsTsvPath);

        CSVReader csvReader = CSVFactory.buildTSVReader(ratingsTsvPath);
        String[] line = csvReader.readNext();
        while (line != null) {
            String imdbId = line[0];
            Movie movie = movies.get(imdbId);
            if (movie != null) {
                String ratingStr = line[1];
                if (StringUtils.isNotBlank(ratingStr) && ratingStr.matches("[0-9]+(\\.[0-9]+)?")) {
                    int rating = new BigDecimal(ratingStr).multiply(BigDecimal.TEN).intValue();
                    movie.setRating(rating);
                } else {
                    log.error("Could not parse rating {} for movie {}", ratingStr, imdbId);
                }
            }
            line = csvReader.readNext();
        }
        csvReader.close();
    }

    @SneakyThrows
    private static HashMap<String, Movie> parseMoviesTitlesTsv() {
        String moviesBasicsTsvPath = "imdb.data/title.basics.only.movies.tsv";
        log.info("Parsing {} into movies HashMap", moviesBasicsTsvPath);

        CSVReader csvReader = CSVFactory.buildTSVReader(moviesBasicsTsvPath);

        HashMap<String, Movie> movies = new HashMap<>();
        String[] line = csvReader.readNext();
        while (line != null) {
            Movie movie = new Movie();
            movie.setImdbId(line[0]);
            movie.setTitle(line[2]);
            String year = line[5];
            if (StringUtils.isNotBlank(year) && year.matches("[0-9]+")) {
                movie.setYear(Integer.parseInt(year));
            }
            String genres = line[8];
            movie.setGenres("N".equals(genres) ? "N/A" : genres);
            movies.put(movie.getImdbId(), movie);
            line = csvReader.readNext();
        }
        csvReader.close();
        return movies;
    }



}
