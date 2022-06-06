package vinicius.cornieri.lets.code.challenge.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import vinicius.cornieri.lets.code.challenge.domain.service.GameService;
import vinicius.cornieri.lets.code.challenge.generated.api.GameApi;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameStartResponseDto;

@RestController
@RequiredArgsConstructor
public class GameController implements GameApi {

    private final GameService gameService;

    @Override
    public ResponseEntity<GameStartResponseDto> gameStartPost() {
        return new ResponseEntity<>(gameService.startGame(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GameChooseResponseDto> gameChoosePost(GameChooseRequestDto gameChooseRequestDto) {
        return ResponseEntity.ok(gameService.processChoice(gameChooseRequestDto));
    }

}