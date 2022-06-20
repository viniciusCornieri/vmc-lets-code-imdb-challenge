package vinicius.cornieri.lets.code.challenge.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class PlayerNotFoundException extends ResponseStatusException {

    public static final String ERROR_MESSAGE_TEMPLATE = "Not found active player to the given username %s";

    public PlayerNotFoundException(String username) {
        super(HttpStatus.FORBIDDEN, String.format(ERROR_MESSAGE_TEMPLATE, username));
        log.warn(String.format(ERROR_MESSAGE_TEMPLATE, username));
    }

}
