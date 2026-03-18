package by.michael.noughtsandcrosses.web.exception;

import by.michael.noughtsandcrosses.domain.exception.GameIsAlredyOverException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return errors;
  }

  @ExceptionHandler(GameIsAlredyOverException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleGameOver(GameIsAlredyOverException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalArgument(IllegalArgumentException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleJsonParserError(HttpMessageNotReadableException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGenericError(HttpClientErrorException.Unauthorized ex) {
    return "Error: Сначала войдите в свой аккаунт!";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGenericError(Exception ex) {
    return "Error: " + ex.getMessage();
  }
}
