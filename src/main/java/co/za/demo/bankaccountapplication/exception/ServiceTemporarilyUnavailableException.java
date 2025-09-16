package co.za.demo.bankaccountapplication.exception;

/**
 * Exception thrown when infrastructure services are temporarily unavailable.
 * This should result in HTTP 503 Service Unavailable responses.
 */
public class ServiceTemporarilyUnavailableException extends RuntimeException {

  /**
   * Constructs a new ServiceTemporarilyUnavailableException with the specified detail message.
   *
   * @param message the detail message
   */
  public ServiceTemporarilyUnavailableException(String message) {
    super(message);
  }

  /**
   * Constructs a new ServiceTemporarilyUnavailableException with the specified detail message
   * and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public ServiceTemporarilyUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
