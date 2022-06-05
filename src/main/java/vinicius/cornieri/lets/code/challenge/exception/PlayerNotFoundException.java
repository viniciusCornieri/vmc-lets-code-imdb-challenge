package vinicius.cornieri.lets.code.challenge.exception;

public class PlayerNotFoundException extends BadRequestException {

    public static final String ERROR_MESSAGE = "Not found active player";

    public PlayerNotFoundException() {
        super(ERROR_MESSAGE);
    }

}
