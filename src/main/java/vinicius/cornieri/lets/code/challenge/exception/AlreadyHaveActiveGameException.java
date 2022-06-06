package vinicius.cornieri.lets.code.challenge.exception;

public class AlreadyHaveActiveGameException extends BadRequestException {

    public AlreadyHaveActiveGameException() {
        super("Player have already an active game started, continue playing at /game/current and /game/choose endpoints");
    }

}
