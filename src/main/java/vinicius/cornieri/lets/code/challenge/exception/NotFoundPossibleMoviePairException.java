package vinicius.cornieri.lets.code.challenge.exception;

public class NotFoundPossibleMoviePairException extends BadRequestException {

    public static final String ERROR_MESSAGE =
        "Couldn't find a distinct new combination of movies for your next round. Please try again in a few moments.";

    public NotFoundPossibleMoviePairException() {
        super(ERROR_MESSAGE);
    }
}
