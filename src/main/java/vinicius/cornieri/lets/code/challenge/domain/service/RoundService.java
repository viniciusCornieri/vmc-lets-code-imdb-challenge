package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.persistence.RoundRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundService {


    private final MovieService movieService;

    private final RoundRepository roundRepository;

    public void createNewRound(Game game) {
        Round round = new Round();
        round.setRoundNumber(calculateRoundNumber(game));
        round.setGame(game);
        game.setCurrentRound(round);
        round.setFirstMovieOption(movieService.getRandomMovie());
        round.setSecondMovieOption(movieService.getRandomMovie());
        roundRepository.saveAndFlush(round);
    }

    private int calculateRoundNumber(Game game){
        return roundRepository.countByGame(game);
    }

}
