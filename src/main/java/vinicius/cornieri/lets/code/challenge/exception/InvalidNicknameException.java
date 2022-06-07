package vinicius.cornieri.lets.code.challenge.exception;

public class InvalidNicknameException extends BadRequestException {

    public static final String ERROR_MESSAGE_TEMPLATE = "Nickname %s already exists";

    public InvalidNicknameException(String nickname) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, nickname));
    }
}
