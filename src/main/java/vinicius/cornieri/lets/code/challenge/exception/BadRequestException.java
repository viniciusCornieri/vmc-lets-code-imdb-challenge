package vinicius.cornieri.lets.code.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception used to map bad request responses on the app
 */
public class BadRequestException extends ResponseStatusException {

    protected BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
