package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.exception.InvalidNicknameException;
import vinicius.cornieri.lets.code.challenge.exception.PlayerNotFoundException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerWithApikeyDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerWithScoreDto;
import vinicius.cornieri.lets.code.challenge.persistence.PlayerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerWithApikeyDto createNewPlayer(String nickname) {
        if (playerRepository.existsByNickname(nickname)) {
            throw new InvalidNicknameException(nickname);
        }

        Player player = new Player();
        player.setNickname(nickname);
        player.setApiKey(UUID.randomUUID().toString());
        playerRepository.saveAndFlush(player);

        return new PlayerWithApikeyDto()
            .apiKey(player.getApiKey())
            .nickname(player.getNickname());
    }

    public void updatePlayerCurrentGame(Player player, Game game) {
        player.setCurrentGame(game);
        playerRepository.saveAndFlush(player);
    }

    public Player findCurrentPlayer(String apiKey) {
        return playerRepository.findByApiKey(apiKey).orElseThrow(() -> new PlayerNotFoundException(apiKey));
    }

    public List<PlayerWithScoreDto> getLeaderboard() {
        List<Player> score = playerRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
        return score.stream().map(PlayerService::toPlayerDto).collect(Collectors.toList());
    }

    private static PlayerWithScoreDto toPlayerDto(Player player) {
        return new PlayerWithScoreDto().nickname(player.getNickname()).score(player.getScore());
    }

    public void score(Game currentGame) {
        Player player = currentGame.getPlayer();
        int byNewScore = currentGame.getCurrentRound().getRoundNumber() - currentGame.getFailuresCount();
        if (byNewScore > 0) {
            player.incrementScore(byNewScore);
            playerRepository.saveAndFlush(player);
        }
    }

}
