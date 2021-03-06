package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.domain.service.mapper.CurrentGameResponseDtoMapper;
import vinicius.cornieri.lets.code.challenge.domain.service.mapper.GameChooseResponseDtoMapper;
import vinicius.cornieri.lets.code.challenge.exception.ActiveGameNotFoundException;
import vinicius.cornieri.lets.code.challenge.exception.AlreadyHaveActiveGameException;
import vinicius.cornieri.lets.code.challenge.exception.InvalidRoundNumberException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.CurrentGameResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;
import vinicius.cornieri.lets.code.challenge.persistence.GameRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GameService {

    private final RoundService roundService;
    private final GameRepository gameRepository;
    private final PlayerService playerService;

    @Value("${game.maximum-failure-allowed:3}")
    private int maximumFailureAllowed;

    public CurrentGameResponseDto startGame(String apiKey) {
        Player activePlayer = getCurrentPlayer(apiKey);
        if (playerHasActiveGame(activePlayer)) {
            log.info("Player {} has already an active game", activePlayer);
            throw new AlreadyHaveActiveGameException();
        }

        Game newGame = new Game();
        newGame.setPlayer(activePlayer);
        gameRepository.save(newGame);
        roundService.createNewRound(newGame);

        playerService.updatePlayerCurrentGame(activePlayer, newGame);
        return CurrentGameResponseDtoMapper.INSTANCE.fromGame(newGame);
    }

    public CurrentGameResponseDto getCurrentActiveGame(String apikey) {
        Game currentGame = getPlayerCurrentGame(apikey);
        return CurrentGameResponseDtoMapper.INSTANCE.fromGame(currentGame);
    }

    public GameChooseResponseDto processChoice(String apiKey, GameChooseRequestDto choice) {
        Game currentGame = getPlayerCurrentGame(apiKey);

        Round lastRound = currentGame.getCurrentRound();
        if (choice.getRoundNumber() != lastRound.getRoundNumber()) {
            throw new InvalidRoundNumberException(choice.getRoundNumber(), lastRound.getRoundNumber());
        }

        ChoiceDto correctAnswer = calculateCorrectChoice(lastRound);
        boolean wasAnsweredCorrectly = choice.getChoice() == correctAnswer;
        lastRound.setWasAnsweredCorrectly(wasAnsweredCorrectly);
        if (!wasAnsweredCorrectly) {
            currentGame.incrementFailures();
        }

        if (currentGame.getFailuresCount() >= maximumFailureAllowed) {
            finishGame(currentGame);
        } else {
            roundService.createNewRound(currentGame);
        }

        return GameChooseResponseDtoMapper.INSTANCE.fromGameAndLastRound(currentGame, lastRound, correctAnswer);
    }

    public void stopGame(String apiKey) {
        Game currentGame = getPlayerCurrentGame(apiKey);
        currentGame.incrementFailures();
        currentGame.getCurrentRound().setWasAnsweredCorrectly(false);
        finishGame(currentGame);
        gameRepository.saveAndFlush(currentGame);
    }

    private void finishGame(Game currentGame) {
        log.info("Finishing game {}", currentGame);
        currentGame.setFinished(true);
        currentGame.setFinishedAt(ZonedDateTime.now());
        playerService.score(currentGame);
    }

    private Game getPlayerCurrentGame(String apiKey) {
        Player activePlayer = getCurrentPlayer(apiKey);
        if (!playerHasActiveGame(activePlayer)) {
            log.info("Player {} has not an active game to choose", activePlayer);
            throw new ActiveGameNotFoundException();
        }

        Game currentGame = activePlayer.getCurrentGame();
        return currentGame;
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

    private Player getCurrentPlayer(String apiKey){
        return playerService.findCurrentPlayer(apiKey);
    }

}
