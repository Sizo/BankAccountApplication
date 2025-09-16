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

    // Determine if this is a business validation error (422) or input validation error (400)
    boolean isBusinessValidation = isBusinessValidationError(ex.getMessage());
    HttpStatus status = isBusinessValidation
        ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST;

    String title = isBusinessValidation ? "Business Validation Failed" : "Invalid Input";
    String type = isBusinessValidation ? "BusinessValidationError" : "IllegalArgumentException";

    Problem problem = new Problem();
    problem.setType(type);
    problem.setTitle(title);
    problem.setStatus(status.value());
    problem.setDetail(ex.getMessage());
    problem.setInstance(request.getDescription(false));
    problem.setTraceId(traceId);

    log.error("{} [{}]: {}", title, traceId, ex.getMessage(), ex);

    return new ResponseEntity<>(problem, status);
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
        .map(this::getHumanReadableErrorMessage)
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
   * Converts technical validation error messages to human-readable ones.
   *
   * @param error the field error from validation
   * @return a human-readable error message
   */
  private String getHumanReadableErrorMessage(
      org.springframework.validation.FieldError error) {
    String field = error.getField();
    String defaultMessage = error.getDefaultMessage();

    if ("amount".equals(field) && defaultMessage != null) {
      if (defaultMessage.contains("must match")) {
        return "amount: must be a positive number (e.g., 100.50)";
      }
    }

    if ("accountNumber".equals(field) && defaultMessage != null) {
      if (defaultMessage.contains("must match")) {
        return "accountNumber: must be exactly 9 digits";
      }
    }

    return field + ": " + cleanValidationMessage(defaultMessage);
  }

  /**
   * Cleans up technical validation messages to be more user-friendly.
   *
   * @param message the original validation message
   * @return a cleaned, more readable message
   */
  private String cleanValidationMessage(String message) {
    if (message == null) {
      return "is invalid";
    }

    // Replace regex pattern messages with user-friendly ones
    if (message.contains("must match")) {
      return "has an invalid format";
    }

    return message;
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

  /**
   * Determines if an exception message represents a business validation error.
   *
   * @param message the exception message
   * @return true if this is a business validation error, false otherwise
   */
  private boolean isBusinessValidationError(String message) {
    if (message == null) {
      return false;
    }

    // Business validation errors that should return 422
    return message.contains("Insufficient funds")
        || message.contains("Account not found")
        || message.contains("Account is closed")
        || message.contains("Daily limit exceeded")
        || message.contains("Transaction not allowed");
  }
}
