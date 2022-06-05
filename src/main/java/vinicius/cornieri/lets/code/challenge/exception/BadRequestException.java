package vinicius.cornieri.lets.code.challenge.exception;

/**
 * Exception used to map bad request responses on the app
 */
public class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }

}
