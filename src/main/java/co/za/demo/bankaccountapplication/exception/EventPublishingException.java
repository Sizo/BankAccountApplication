package co.za.demo.bankaccountapplication.exception;

/**
 * Exception thrown when event publishing fails.
 */
public class EventPublishingException extends Exception {

  /**
   * Constructs a new EventPublishingException with the specified detail message.
   *
   * @param message the detail message
   */
  public EventPublishingException(String message) {
    super(message);
  }

  /**
   * Constructs a new EventPublishingException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public EventPublishingException(String message, Throwable cause) {
    super(message, cause);
  }
}
