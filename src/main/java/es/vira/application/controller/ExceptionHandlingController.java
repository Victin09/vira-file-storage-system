package es.vira.application.controller;

import es.vira.application.exception.UserNotFoundException;
import es.vira.application.exception.UsernameAlreadyExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Log4j2
@ControllerAdvice
public class ExceptionHandlingController {

    public static final String LOG_JSON_TEMPLATE = "{\n" +
            "  \"username\": \"%s\",\n" +
            "  \"IP\": \"%s\",\n" +
            "  \"timestamp\": \"%d\",\n" +
            "  \"exception\": \"%s\",\n" +
            "  \"message\": \"%s\",\n" +
            "  \"requestURI\": \"%s\"\n" +
            "}\n";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex,
                                                  HttpServletRequest request,
                                                  Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        return getResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generateLogMessage(Exception ex, HttpServletRequest request, Principal principal) {
        return String.format(LOG_JSON_TEMPLATE, principal.getName(),
                request.getRemoteAddr(),
                System.currentTimeMillis(),
                ex.getClass().getName(),
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(Exception ex,
                                                              HttpServletRequest request,
                                                              Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        String exMessage = ex.getMessage();
        return getResponseEntity(exMessage == null ? "User not found in the database" : exMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(Exception ex,
                                                                        HttpServletRequest request,
                                                                        Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(status.getReasonPhrase(), status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex,
                                                              HttpServletRequest request,
                                                              Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        return getResponseEntity("Access is denied", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(Exception ex,
                                                                       HttpServletRequest request,
                                                                       Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        String exMessage = ex.getMessage();
        return getResponseEntity(exMessage == null ? "Username already exists" : exMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Object> handleConversionFailedException(Exception ex,
                                                                  HttpServletRequest request,
                                                                  Principal principal) {
        LOGGER.error(generateLogMessage(ex, request, principal), ex);
        return getResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> getResponseEntity(String exceptionMessage,
                                                     HttpStatus status) {
        return new ResponseEntity<>(exceptionMessage, status);
    }
}
