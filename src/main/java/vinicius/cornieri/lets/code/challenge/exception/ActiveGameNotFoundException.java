package vinicius.cornieri.lets.code.challenge.exception;

public class ActiveGameNotFoundException extends BadRequestException {

    public static final String ERROR_MESSAGE =
        "Player does not have an active game started, start a game at /game/start";

    public ActiveGameNotFoundException() {
        super(ERROR_MESSAGE);
    }

}
