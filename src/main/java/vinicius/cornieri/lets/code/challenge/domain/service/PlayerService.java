package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Player;
import vinicius.cornieri.lets.code.challenge.exception.InvalidNicknameException;
import vinicius.cornieri.lets.code.challenge.exception.PlayerNotFoundException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerWithScoreDto;
import vinicius.cornieri.lets.code.challenge.persistence.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    public static final String DEFAULT_PLAYER_ROLE = "GAME";
    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerResponseDto createNewPlayer(String username, String password) {
        return createNewPlayer(username, password, DEFAULT_PLAYER_ROLE);
    }

    public PlayerResponseDto createNewPlayer(String username, String password, String role) {
        if (playerRepository.existsByUsername(username)) {
            throw new InvalidNicknameException(username);
        }

        Player player = new Player();
        player.setUsername(username);
        player.setPassword(encode(password));
        player.addNewRole(role);
        playerRepository.saveAndFlush(player);

        return new PlayerResponseDto()
            .username(player.getUsername());
    }

    private String encode(String password) {
        return password;
    }

    public void updatePlayerCurrentGame(Player player, Game game) {
        player.setCurrentGame(game);
        playerRepository.saveAndFlush(player);
    }

    public Player findCurrentPlayer(String username) {
        return playerRepository.findByUsername(username).orElseThrow(() -> new PlayerNotFoundException(username));
    }

    public List<PlayerWithScoreDto> getLeaderboard() {
        List<Player> score = playerRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
        return score.stream().map(PlayerService::toPlayerDto).collect(Collectors.toList());
    }

    private static PlayerWithScoreDto toPlayerDto(Player player) {
        return new PlayerWithScoreDto().nickname(player.getUsername()).score(player.getScore());
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
