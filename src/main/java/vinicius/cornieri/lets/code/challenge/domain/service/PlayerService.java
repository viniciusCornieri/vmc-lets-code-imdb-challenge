package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.exception.PlayerNotFoundException;
import vinicius.cornieri.lets.code.challenge.persistence.PlayerRepository;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player createNewPlayer(String name, String email) {
        Player player = new Player();
        player.setEmail(email);
        player.setName(name);

        return playerRepository.saveAndFlush(player);
    }

    public void updatePlayerCurrentGame(Player player, Game game) {
        player.setCurrentGame(game);
        playerRepository.saveAndFlush(player);
    }

    public Player findActivePlayer() {
        return playerRepository.findById(1L)
            .orElseThrow(PlayerNotFoundException::new);
    }

}
