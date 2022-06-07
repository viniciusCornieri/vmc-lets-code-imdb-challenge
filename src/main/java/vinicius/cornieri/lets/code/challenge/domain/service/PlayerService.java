package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.exception.PlayerNotFoundException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerDto;
import vinicius.cornieri.lets.code.challenge.persistence.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void updatePlayerCurrentGame(Player player, Game game) {
        player.setCurrentGame(game);
        playerRepository.saveAndFlush(player);
    }

    public Player findCurrentPlayer(String apiKey) {
        return playerRepository.findByApiKey(apiKey).orElseThrow(() -> new PlayerNotFoundException(apiKey));
    }

    public List<PlayerDto> getLeaderboard() {
        List<Player> score = playerRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
        return score.stream().map(PlayerService::toPlayerDto).collect(Collectors.toList());
    }

    private static PlayerDto toPlayerDto(Player player) {
        return new PlayerDto().nickname(player.getNickname()).score(player.getScore());
    }

}
