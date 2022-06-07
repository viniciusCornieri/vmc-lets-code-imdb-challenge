package vinicius.cornieri.lets.code.challenge.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.BadRequestResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.BadRequestResponseFieldsDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String VALIDATION_FAILED_ERROR_MESSAGE = "Validation failed:";
    public static final String REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE = "Required request body is missing";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        List<BadRequestResponseFieldsDto> fieldsErrors = ex.getBindingResult()
            .getFieldErrors().stream()
            .map(err -> new BadRequestResponseFieldsDto()
                .field(err.getField())
                .fieldError(err.getDefaultMessage())
                .rejectedValue(Objects.toString(err.getRejectedValue()))

            )
            .distinct()
            .collect(Collectors.toList());

        return buildBadRequestEntity(headers, VALIDATION_FAILED_ERROR_MESSAGE, fieldsErrors);
    }

    private ResponseEntity<Object> buildBadRequestEntity(HttpHeaders headers,
                                                         String errorMessage,
                                                         List<BadRequestResponseFieldsDto> fieldsErrors) {
        return ResponseEntity
            .badRequest()
            .headers(headers)
            .body(new BadRequestResponseDto()
                .message(errorMessage)
                .timestamp(ZonedDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .fields(fieldsErrors)
            );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        String localizedMessage = ex.getMostSpecificCause().getLocalizedMessage();
        if (localizedMessage.contains(REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE)) {
            return buildBadRequestEntity(headers, REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE,null);
        }
        return buildBadRequestEntity(headers, localizedMessage,null);
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    protected ResponseEntity<Object> handleMissingRequestHeader(MissingRequestHeaderException ex,
                                                                  WebRequest request) {

        if ("API-KEY".equals(ex.getHeaderName())) {
            return new ResponseEntity<>(
                new BadRequestResponseDto().message("Need to inform a valid Player API-KEY header to access the game"),
                HttpStatus.UNAUTHORIZED);
        }

        return buildBadRequestEntity(null, "Missing required header " + ex.getHeaderName(), null);
    }

}
