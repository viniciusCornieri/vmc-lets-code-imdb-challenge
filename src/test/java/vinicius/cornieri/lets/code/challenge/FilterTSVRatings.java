package vinicius.cornieri.lets.code.challenge;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.service.csv.CSVFactory;

import java.io.FileWriter;
import java.io.Reader;
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

import static vinicius.cornieri.lets.code.challenge.domain.service.csv.CSVFactory.buildReader;

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
            .filter(m -> m.getRating() != null)
            .collect(Collectors.toList());
        sbc.write(new ArrayList<>(values));
        writer.close();
    }

    @SneakyThrows
    private static void processRatingsTSV(HashMap<String, Movie> movies) {
        String ratingsTsvPath = "imdb.data/title.ratings.tsv";
        log.info("Processing {} filling rating of movies HashMap", ratingsTsvPath);

        CSVReader csvReader = buildTSVReader(ratingsTsvPath);
        String[] line = csvReader.readNext();
        while (line != null) {
            String imdbId = line[0];
            Movie movie = movies.get(imdbId);
            if (movie != null) {
                String ratingStr = line[1];
                BigDecimal rating = toBigDecimal(imdbId, ratingStr);
                movie.setRating(rating);

                String numVotesStr = line[2];
                BigDecimal numVotes = toBigDecimal(imdbId, numVotesStr);
                movie.setNumVotes(numVotes);
            }
            line = csvReader.readNext();
        }
        csvReader.close();
    }

    private static BigDecimal toBigDecimal(String imdbId, String numericStr) {
        if (StringUtils.isNotBlank(numericStr) && numericStr.matches("[0-9]+(\\.[0-9]+)?")) {
            return new BigDecimal(numericStr);
        }
        log.error("Could not parse rating {} for movie {}", numericStr, imdbId);
        return null;
    }

    @SneakyThrows
    private static HashMap<String, Movie> parseMoviesTitlesTsv() {
        String moviesBasicsTsvPath = "imdb.data/title.basics.only.movies.tsv";
        log.info("Parsing {} into movies HashMap", moviesBasicsTsvPath);

        CSVReader csvReader = buildTSVReader(moviesBasicsTsvPath);

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

    @SneakyThrows
    private static CSVReader buildCSVReader(String csvPath, CSVParser parser) {
        Reader reader = buildReader(csvPath);

        return new CSVReaderBuilder(reader)
            .withSkipLines(1)
            .withCSVParser(parser)
            .build();
    }

    public static CSVReader buildTSVReader(String csvPath) {
        CSVParser tsvParser = buildTSVParser();

        return buildCSVReader(csvPath, tsvParser);
    }

    private static CSVParser buildTSVParser() {
        return new CSVParserBuilder()
            .withSeparator('\t')
            .withIgnoreQuotations(true)
            .build();
    }

}
