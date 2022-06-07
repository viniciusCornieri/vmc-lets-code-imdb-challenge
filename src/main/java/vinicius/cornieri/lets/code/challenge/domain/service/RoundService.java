package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.exception.NotFoundPossibleMoviePairException;
import vinicius.cornieri.lets.code.challenge.persistence.RoundRepository;

import static vinicius.cornieri.lets.code.challenge.domain.service.MovieIdComparator.higherIdMovie;
import static vinicius.cornieri.lets.code.challenge.domain.service.MovieIdComparator.lowerIdMovie;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoundService {

    private final MovieService movieService;

    private final RoundRepository roundRepository;

    @Value("${round.select-movies.max-depth:10}")
    private int maxDepth;

    public void createNewRound(Game game) {
        Round newRound = selectMoviesAndCreateRound(game);
        newRound.setRoundNumber(calculateRoundNumber(game));
        newRound.setGame(game);
        game.setCurrentRound(newRound);
        log.info("Creating a new round number {} for game {} with movies first {} and second {}",
            newRound.getRoundNumber(), game.getId(), newRound.getFirstMovieOption().getId(),
            newRound.getSecondMovieOption().getId());
        roundRepository.save(newRound);
    }

    private Round selectMoviesAndCreateRound(Game game) {
        return selectMoviesAndCreateRound(game, 0);
    }

    private Round selectMoviesAndCreateRound(Game game, int depth) {
        if (depth > maxDepth) {
            log.error("Max select movies depth {} exceeded", maxDepth);
            throw new NotFoundPossibleMoviePairException();
        }

        Movie movieA = movieService.getRandomMovie();
        Movie movieB = movieService.getRandomMovie();
        Movie firstMovie = lowerIdMovie(movieA, movieB);
        Movie secondMovie = higherIdMovie(movieA, movieB);
        if (!movieA.equals(movieB) && validateMoviePair(game, firstMovie, secondMovie)) {
            Round round = new Round();
            round.setFirstMovieOption(firstMovie);
            round.setSecondMovieOption(secondMovie);
            return round;
        }
        log.warn("Game {} firstMovie {} secondMovie {} is not a valid combination trying again", game.getId(),
            firstMovie.getId(), secondMovie.getId());
        return selectMoviesAndCreateRound(game, depth + 1);
    }

    private boolean validateMoviePair(Game game, Movie firstMovie, Movie secondMovie) {
        return !roundRepository.existsByGameAndFirstMovieOptionAndSecondMovieOption(game, firstMovie, secondMovie);
    }

    private int calculateRoundNumber(Game game) {
        return roundRepository.countByGame(game);
    }

}
