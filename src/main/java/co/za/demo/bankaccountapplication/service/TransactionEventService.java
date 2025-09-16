package co.za.demo.bankaccountapplication.service;

import co.za.demo.bankaccountapplication.exception.EventPublishingException;
import co.za.demo.bankaccountapplication.integration.EventPublisher;
import co.za.demo.bankaccountapplication.model.event.WithdrawalEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing transaction-related events to external systems.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionEventService {

  private final EventPublisher<WithdrawalEvent> eventPublisher;

  @Value("${app.sns.withdrawal-topic:arn:aws:sns:us-east-1:123456789012:withdrawal-events}")
  private String withdrawalTopic;

  /**
   * Publishes a withdrawal event to the configured SNS topic.
   *
   * @param accountNumber the account number involved in the withdrawal
   * @param amount        the withdrawal amount
   * @param status        the withdrawal status (e.g., "SUCCESS", "FAILED", "DECLINED")
   */
  public void publishWithdrawalEvent(String accountNumber, BigDecimal amount, String status) {
    try {
      WithdrawalEvent event = new WithdrawalEvent(amount, accountNumber, status);
      eventPublisher.publish(withdrawalTopic, accountNumber, event);
      log.info("Successfully published withdrawal event for account: {} with status: {}",
          accountNumber, status);
    } catch (EventPublishingException e) {
      // Log the error but don't fail the calling operation
      log.error("Failed to publish withdrawal event for account: {} - {}",
          accountNumber, e.getMessage(), e);
    }
  }

  /**
   * Publishes a withdrawal event.
   *
   * @param accountNumber the account number involved in the withdrawal
   * @param amountString  the withdrawal amount as string
   * @param status        the withdrawal status (e.g., "SUCCESS", "FAILED", "DECLINED")
   */
  public void publishWithdrawalEvent(String accountNumber, String amountString, String status) {
    try {
      BigDecimal amount = new BigDecimal(amountString);
      publishWithdrawalEvent(accountNumber, amount, status);
    } catch (NumberFormatException e) {
      log.error("Invalid amount format for withdrawal event: {} - {}", amountString,
          e.getMessage());
    }
  }

  /**
   * Publishes a withdrawal failure event for business validation failures.
   * Used when withdrawal fails due to business rules (insufficient funds, etc.)
   *
   * @param accountNumber the account number involved in the withdrawal
   * @param amount        the attempted withdrawal amount
   * @param reason        the reason for failure
   */
  public void publishWithdrawalFailedEvent(String accountNumber, BigDecimal amount, String reason) {
    publishWithdrawalEvent(accountNumber, amount, "DECLINED");
    log.info("Published withdrawal declined event for account: {} - Reason: {}", accountNumber,
        reason);
  }

  /**
   * Publishes a withdrawal failure event with string amount.
   *
   * @param accountNumber the account number involved in the withdrawal
   * @param amountString  the attempted withdrawal amount as string
   * @param reason        the reason for failure
   */
  public void publishWithdrawalFailedEvent(String accountNumber, String amountString,
                                           String reason) {
    try {
      BigDecimal amount = new BigDecimal(amountString);
      publishWithdrawalFailedEvent(accountNumber, amount, reason);
    } catch (NumberFormatException e) {
      log.error("Invalid amount format for failed withdrawal event: {} - {}", amountString,
          e.getMessage());
    }
  }
}
