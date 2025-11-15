package com.tfm.bandas.events.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NotFoundException ex) {
    return Map.of("error", "Not Found", "message", ex.getMessage());
  }

  @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(RuntimeException ex) {
    return Map.of("error", "Bad Request", "message", ex.getMessage());
  }

  @ExceptionHandler({ IllegalStateException.class })
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConflict(RuntimeException ex) {
    return Map.of("error", "Conflict", "message", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (a,b) -> a, LinkedHashMap::new));
    return Map.of("error", "Validation Failed", "details", details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (a,b)->a, LinkedHashMap::new));
    return Map.of("error", "Constraint Violation", "details", details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
    String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    return Map.of("error", "Malformed JSON", "message", msg);
  }

  @ExceptionHandler({ AccessDeniedException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, Object> handleDenied(AccessDeniedException ex) {
    return Map.of("error", "Forbidden", "message", "Insufficient permissions");
  }

  @ExceptionHandler(PreconditionRequiredException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED) // 428
  public Map<String, Object> handlePreconditionRequired(RuntimeException ex) {
    return Map.of("error", "Precondition Required", "message", ex.getMessage());
  }

  @ExceptionHandler(PreconditionFailedException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED) // 412
  public Map<String, Object> handlePreconditionFailed(RuntimeException ex) {
    return Map.of("error", "Precondition Failed", "message", ex.getMessage());
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // 409
  public Map<String, Object> handleOptimisticLock(OptimisticLockingFailureException ex) {
    return Map.of("error", "Optimistic Lock", "message", ex.getMessage() != null ? ex.getMessage() : "Concurrent update detected");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneric(Exception ex) {
    return Map.of("error", "Internal Error", "message", ex.getMessage());
  }
}
