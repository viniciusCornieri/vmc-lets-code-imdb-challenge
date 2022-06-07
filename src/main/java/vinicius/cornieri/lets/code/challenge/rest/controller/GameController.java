package vinicius.cornieri.lets.code.challenge.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import vinicius.cornieri.lets.code.challenge.domain.service.GameService;
import vinicius.cornieri.lets.code.challenge.generated.api.GameApi;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.CurrentGameResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;

@RestController
@RequiredArgsConstructor
public class GameController implements GameApi {

    private final GameService gameService;

    @Override
    public ResponseEntity<CurrentGameResponseDto> gamePost(String apiKey) {
        return new ResponseEntity<>(gameService.startGame(apiKey), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GameChooseResponseDto> gameChoosePost(String apiKey, GameChooseRequestDto gameChooseRequestDto) {
        return ResponseEntity.ok(gameService.processChoice(apiKey, gameChooseRequestDto));
    }

    @Override
    public ResponseEntity<Void> gameStopPost(String apiKey) {
        gameService.stopGame(apiKey);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CurrentGameResponseDto> gameGet(String apiKey) {
        return ResponseEntity.ok(gameService.getCurrentActiveGame(apiKey));
    }

}