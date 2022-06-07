package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.exception.PlayerNotFoundException;
import vinicius.cornieri.lets.code.challenge.persistence.PlayerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void updatePlayerCurrentGame(Player player, Game game) {
        player.setCurrentGame(game);
        playerRepository.saveAndFlush(player);
    }

    public Player findCurrentPlayer(String apiKey) {
        return playerRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new PlayerNotFoundException(apiKey));
    }

}
