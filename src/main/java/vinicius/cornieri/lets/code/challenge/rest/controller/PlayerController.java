package vinicius.cornieri.lets.code.challenge.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import vinicius.cornieri.lets.code.challenge.domain.service.PlayerService;
import vinicius.cornieri.lets.code.challenge.generated.api.PlayerApi;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController implements PlayerApi {

    private final PlayerService playerService;

    @Override
    public ResponseEntity<List<PlayerDto>> playerLeaderboardGet() {
        return ResponseEntity.ok(playerService.getLeaderboard());
    }

}
