package vinicius.cornieri.lets.code.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerNotFoundException extends ResponseStatusException {

    public static final String ERROR_MESSAGE_TEMPLATE = "Not found active player to the given apiKey %s";

    public PlayerNotFoundException(String apiKey) {
        super(HttpStatus.UNAUTHORIZED, String.format(ERROR_MESSAGE_TEMPLATE, apiKey));
    }

}
