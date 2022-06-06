package vinicius.cornieri.lets.code.challenge.exception;

public class AlreadyHaveActiveGameException extends BadRequestException {

    public static final String ERROR_MESSAGE =
        "Player have already an active game started, continue playing at /game/current and /game/choose endpoints";

    public AlreadyHaveActiveGameException() {
        super(ERROR_MESSAGE);
    }

}
