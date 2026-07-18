package com.tfm.bandas.events.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NotFoundException ex) {
    return Map.of("error", "No Encontrado", "errorCode", "EVENT_NOT_FOUND", "message", ex.getMessage());
  }

  @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(RuntimeException ex) {
    return Map.of("error", "Petición Inválida", "message", ex.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConflict(ConflictException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", "Conflicto de Datos");
    if (ex.getErrorCode() != null) {
      body.put("errorCode", ex.getErrorCode());
    }
    body.put("message", ex.getMessage());
    return body;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (a,b) -> a, LinkedHashMap::new));
    return Map.of("error", "Errores de Validación", "message", "Uno o más campos no son válidos.", "details", details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (a,b)->a, LinkedHashMap::new));
    return Map.of("error", "Violación de Restricciones", "message", "Uno o más parámetros no son válidos.", "details", details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Petición con cuerpo JSON malformado o no esperado: {}", ex.getMessage());
    return Map.of("error", "JSON Malformado",
            "message", "El cuerpo de la petición no es un JSON válido o no tiene el formato esperado.");
  }

  @ExceptionHandler({ AccessDeniedException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, Object> handleDenied(AccessDeniedException ex) {
    return Map.of("error", "Acceso Denegado", "message", "No tienes permisos para realizar esta operación.");
  }

  @ExceptionHandler(PreconditionRequiredException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED) // 428
  public Map<String, Object> handlePreconditionRequired(RuntimeException ex) {
    return Map.of("error", "Precondición Requerida", "message", ex.getMessage());
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED) // 428
  public Map<String, Object> handleMissingHeader(MissingRequestHeaderException ex) {
    return Map.of("error", "Precondición Requerida", "message", "La cabecera If-Match es obligatoria.");
  }

  @ExceptionHandler(PreconditionFailedException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED) // 412
  public Map<String, Object> handlePreconditionFailed(RuntimeException ex) {
    return Map.of("error", "Precondición Fallida", "message", ex.getMessage());
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // 409
  public Map<String, Object> handleOptimisticLock(OptimisticLockingFailureException ex) {
    return Map.of("error", "Conflicto de Concurrencia", "errorCode", "OPTIMISTIC_LOCK_CONFLICT",
            "message", "Se ha detectado una modificación concurrente de este recurso. Recupera la versión más reciente e inténtalo de nuevo.");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneric(Exception ex) {
    log.error("Error inesperado no controlado", ex);
    return Map.of("error", "Error Interno", "message", "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.");
  }
}
