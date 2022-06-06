package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.domain.service.mapper.GameStartResponseDtoMapper;
import vinicius.cornieri.lets.code.challenge.exception.AlreadyHaveActiveGameException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameStartResponseDto;
import vinicius.cornieri.lets.code.challenge.persistence.GameRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final PlayerService playerService;
    private final RoundService roundService;
    private final GameRepository gameRepository;

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

    private boolean playerHasActiveGame(Player activePlayer) {
        return activePlayer.getCurrentGame() != null && !activePlayer.getCurrentGame().isFinished();
    }

}
