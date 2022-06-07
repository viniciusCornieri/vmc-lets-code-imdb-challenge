package vinicius.cornieri.lets.code.challenge.exception;

public class InvalidRoundNumberException extends BadRequestException {

    public static final String ERROR_MESSAGE_TEMPLATE =
        "Invalid round number %d current round is %d";

    public InvalidRoundNumberException(int roundNumber, int currentRoundNumber) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, roundNumber, currentRoundNumber));
    }
}
