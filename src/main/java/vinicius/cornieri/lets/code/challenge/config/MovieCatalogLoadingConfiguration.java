package vinicius.cornieri.lets.code.challenge.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import vinicius.cornieri.lets.code.challenge.domain.service.MovieService;

import java.time.Duration;
import java.time.Instant;

@ConditionalOnProperty(prefix = "movie.catalog", name="populate", havingValue = "true", matchIfMissing = true)
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MovieCatalogLoadingConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final MovieService movieService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Instant start = Instant.now();
        log.info("Verifying if is needed to populate initial movie catalog");
        if (movieService.isMovieCatalogEmpty()) {
            movieService.populateInitialDataFromCSV();
        }

        log.info("Initial movie catalog loading took {}ms", Duration.between(start, Instant.now()).toMillis());
    }

}
