package co.za.demo.bankaccountapplication.exception;

import co.za.demo.bankaccountapplication.model.dto.Problem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler to manage and format error responses consistently across the
 * application.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  /**
   * Header name for trace ID.
   */
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /**
   * Handles validation errors for method parameters.
   *
   * @param ex      the ConstraintViolationException
   * @param request the web request
   * @return a ResponseEntity containing the Problem details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    String errorMessage = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    // Get trace ID from request header or generate a new one if not present
    String traceId = getTraceId(request);

    Problem problem = new Problem();
    problem.setType("ConstraintViolationException");
    problem.setTitle("Validation Error");
    problem.setStatus(HttpStatus.BAD_REQUEST.value());
    problem.setDetail(errorMessage);
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("Validation error [{}]: {}", traceId, errorMessage, ex);

    return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles illegal argument exceptions.
   *
   * @param ex      the IllegalArgumentException
   * @param request the web request
   * @return a ResponseEntity containing the Problem details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Problem> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {

    // Get trace ID from request header or generate a new one if not present
    String traceId = getTraceId(request);

    Problem problem = new Problem();
    problem.setType("IllegalArgumentException");
    problem.setTitle("Invalid Input");
    problem.setStatus(HttpStatus.BAD_REQUEST.value());
    problem.setDetail(ex.getMessage());
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("Invalid input [{}]: {}", traceId, ex.getMessage(), ex);

    return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles number format exceptions.
   *
   * @param ex      the NumberFormatException
   * @param request the web request
   * @return a ResponseEntity containing the Problem details
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<Problem> handleNumberFormatException(
      NumberFormatException ex, WebRequest request) {

    // Get trace ID from request header or generate a new one if not present
    String traceId = getTraceId(request);

    Problem problem = new Problem();
    problem.setType("NumberFormatError");
    problem.setTitle("Invalid Number Format");
    problem.setStatus(HttpStatus.BAD_REQUEST.value());
    problem.setDetail("The amount must be a valid number: " + ex.getMessage());
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("Invalid number format [{}]: {}", traceId, ex.getMessage(), ex);

    return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles entity not found exceptions.
   *
   * @param ex      the EntityNotFoundException
   * @param request the web request
   * @return a ResponseEntity containing the Problem details
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Problem> handleEntityNotFoundException(
      EntityNotFoundException ex, WebRequest request) {

    // Get trace ID from request header or generate a new one if not present
    String traceId = getTraceId(request);

    Problem problem = new Problem();
    problem.setType("EntityNotFound");
    problem.setTitle("Resource Not Found");
    problem.setStatus(HttpStatus.NOT_FOUND.value());
    problem.setDetail(ex.getMessage());
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("Entity not found [{}]: {}", traceId, ex.getMessage(), ex);

    return new ResponseEntity<>(problem, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles method argument validation errors.
   *
   * @param ex      the MethodArgumentNotValidException
   * @param request the web request
   * @return a ResponseEntity containing the Problem details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Problem> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {

    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));

    // Get trace ID from request header or generate a new one if not present
    String traceId = getTraceId(request);

    Problem problem = new Problem();
    problem.setType("ValidationError");
    problem.setTitle("Request Validation Failed");
    problem.setStatus(HttpStatus.BAD_REQUEST.value());
    problem.setDetail(errorMessage);
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("Request validation error [{}]: {}", traceId, errorMessage, ex);

    return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
  }

  /**
   * Gets the trace ID from the request header or generates a new one if not present.
   *
   * @param request the web request
   * @return the trace ID
   */
  private String getTraceId(WebRequest request) {
    String traceId = request.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isEmpty()) {
      traceId = UUID.randomUUID().toString();
    }
    return traceId;
  }
}
