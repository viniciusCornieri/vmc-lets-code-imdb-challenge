package vinicius.cornieri.lets.code.challenge.domain.service;

import com.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.service.csv.CSVFactory;
import vinicius.cornieri.lets.code.challenge.exception.MovieSearchFailureException;
import vinicius.cornieri.lets.code.challenge.persistence.MovieRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    public static final long FIRST_MOVIE_ID = 1L;

    private final MovieRepository movieRepository;

    @Value("${movie.catalog.path:imdb.data/title.basics.with.ratings.csv}")
    private String movieCatalogCSVPath;

    private Long countCache;

    @Transactional
    public void populateInitialDataFromCSV() {
        log.info("Populating initial data from csv {}", movieCatalogCSVPath);
        CsvToBean<Movie> csvToBean = CSVFactory.buildCSVToBean(movieCatalogCSVPath, Movie.class);

        movieRepository.saveAllAndFlush(csvToBean);

        long count = this.count();
        log.info("{} movies added into the catalog", count);
    }

    public boolean isMovieCatalogEmpty() {
        return movieRepository.count() == 0L;
    }

    public long count() {
        if (countCache == null) {
            countCache = movieRepository.count();
        }
        return countCache;
    }

    public Movie getRandomMovie() {
        long randomMovieId = getRandomMovieId();
        return movieRepository.findById(randomMovieId)
            .orElseThrow(() -> new MovieSearchFailureException("Not found movie with id " + randomMovieId));
    }

    private long getRandomMovieId() {
        long lastMovieIdExclusive = this.count() + 1L;
        return RandomUtils.nextLong(FIRST_MOVIE_ID, lastMovieIdExclusive);
    }

}
