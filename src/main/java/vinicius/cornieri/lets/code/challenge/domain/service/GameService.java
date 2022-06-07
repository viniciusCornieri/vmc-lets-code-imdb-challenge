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
public class GameService {

    private final PlayerService playerService;
    private final RoundService roundService;
    private final GameRepository gameRepository;

    @Value("${game.maximum-failure-allowed:3}")
    private int maximumFailureAllowed;

    @Transactional
    public CurrentGameResponseDto startGame() {
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
        return CurrentGameResponseDtoMapper.INSTANCE.fromGame(newGame);
    }

    @Transactional
    public CurrentGameResponseDto getCurrentActiveGame() {
        Game currentGame = getPlayerCurrentGame();
        return CurrentGameResponseDtoMapper.INSTANCE.fromGame(currentGame);
    }

    @Transactional
    public GameChooseResponseDto processChoice(GameChooseRequestDto choice) {
        Game currentGame = getPlayerCurrentGame();

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

    @Transactional
    public void stopGame() {
        Game currentGame = getPlayerCurrentGame();
        finishGame(currentGame);
        gameRepository.saveAndFlush(currentGame);
    }

    private void finishGame(Game currentGame) {
        currentGame.setFinished(true);
        currentGame.setFinishedAt(ZonedDateTime.now());
        currentGame.setCurrentRound(null);
    }

    private Game getPlayerCurrentGame() {
        Player activePlayer = playerService.findActivePlayer();
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

}
