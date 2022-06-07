package vinicius.cornieri.lets.code.challenge.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import vinicius.cornieri.lets.code.challenge.domain.service.PlayerService;
import vinicius.cornieri.lets.code.challenge.generated.api.PlayerApi;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerWithApikeyDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerWithScoreDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController implements PlayerApi {

    private final PlayerService playerService;

    @Override
    public ResponseEntity<List<PlayerWithScoreDto>> playerLeaderboardGet() {
        return ResponseEntity.ok(playerService.getLeaderboard());
    }

    @Override
    public ResponseEntity<PlayerWithApikeyDto> playerPost(PlayerDto playerDto) {
        return new ResponseEntity<>(playerService.createNewPlayer(playerDto.getNickname()), HttpStatus.CREATED);
    }

}
