package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.domain.service.mapper.GameChooseResponseDtoMapper;
import vinicius.cornieri.lets.code.challenge.domain.service.mapper.GameStartResponseDtoMapper;
import vinicius.cornieri.lets.code.challenge.exception.ActiveGameNotFoundException;
import vinicius.cornieri.lets.code.challenge.exception.AlreadyHaveActiveGameException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameStartResponseDto;
import vinicius.cornieri.lets.code.challenge.persistence.GameRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final PlayerService playerService;
    private final RoundService roundService;
    private final GameRepository gameRepository;

    @Value("${game.maximum-failure-allowed:3}")
    private int maximumFailureAllowed;

    @Transactional
    public GameStartResponseDto startGame() {
        Player activePlayer = playerService.findActivePlayer();
        if (playerHasActiveGame(activePlayer)) {
            log.info("Player {} has already an active game", activePlayer);
            throw new AlreadyHaveActiveGameException();
        }

        Game newGame = new Game();
        newGame.setPlayer(activePlayer);
        gameRepository.save(newGame);
        roundService.createNewRound(newGame);

        playerService.updatePlayerCurrentGame(activePlayer, newGame);
        return GameStartResponseDtoMapper.INSTANCE.fromGame(newGame);
    }

    @Transactional
    public GameChooseResponseDto processChoice(GameChooseRequestDto choice) {
        Player activePlayer = playerService.findActivePlayer();
        if (!playerHasActiveGame(activePlayer)) {
            log.info("Player {} has not an active game to choose", activePlayer);
            throw new ActiveGameNotFoundException();
        }

        Game currentGame = activePlayer.getCurrentGame();

        Round currentRound = currentGame.getCurrentRound();
//        if (choice.getRoundNumber() != currentRound.getRoundNumber()) {
//            throw new InvalidRoundNumberException(choice.getRoundNumber(), currentRound.getRoundNumber());
//        }

        ChoiceDto correctAnswer = calculateCorrectChoice(currentRound);
        boolean wasAnsweredCorrectly = choice.getChoice() == correctAnswer;
        currentRound.setWasAnsweredCorrectly(wasAnsweredCorrectly);
        if (!wasAnsweredCorrectly) {
            currentGame.incrementFailures();
        }

        if (currentGame.getFailuresCount() >= maximumFailureAllowed) {
            currentGame.setFinished(true);
            currentGame.setFinishedAt(ZonedDateTime.now());
        } else {
            roundService.createNewRound(currentGame);
        }

        return GameChooseResponseDtoMapper.INSTANCE.fromGameAndLastRound(currentGame, currentRound, correctAnswer);
    }

    private ChoiceDto calculateCorrectChoice(Round currentRound) {
        BigDecimal firstMovieScore = currentRound.getFirstMovieOption().getScore();
        BigDecimal secondMovieScore = currentRound.getSecondMovieOption().getScore();
        if (firstMovieScore.compareTo(secondMovieScore) > 0) {
            return ChoiceDto.FIRST;
        }

        return ChoiceDto.SECOND;
    }

    private boolean playerHasActiveGame(Player activePlayer) {
        return activePlayer.getCurrentGame() != null && !activePlayer.getCurrentGame().isFinished();
    }

}
