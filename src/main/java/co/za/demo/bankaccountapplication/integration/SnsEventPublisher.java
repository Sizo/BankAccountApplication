package co.za.demo.bankaccountapplication.integration;

import co.za.demo.bankaccountapplication.exception.EventPublishingException;
import co.za.demo.bankaccountapplication.model.event.WithdrawalEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SNS implementation of the EventPublisher for publishing withdrawal events.
 * Currently configured for demo mode with enhanced mock functionality.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("VariableDeclarationUsageDistance")
public class SnsEventPublisher implements EventPublisher<WithdrawalEvent> {

  @Value("${app.demo.sns.enabled:true}")
  private boolean demoMode;

  @Value("${app.demo.sns.simulate-delay:true}")
  private boolean simulateDelay;

  @Value("${app.demo.sns.failure-rate:0.0}")
  private double failureRate;

  // TODO: Inject AWS SNS client when AWS dependencies are added
  // private final SnsClient snsClient;

  @Override
  public void publish(String topic, WithdrawalEvent event) throws EventPublishingException {
    publish(topic, event.getAccountNumber(), event);
  }

  @Override
  public void publish(String topic, String key, WithdrawalEvent event)
      throws EventPublishingException {
    if (demoMode) {
      publishDemoMode(topic, key, event);
    } else {
      publishProductionMode(topic, key, event);
    }
  }

  /**
   * Enhanced demo mode publishing with realistic SNS simulation.
   */
  private void publishDemoMode(String topic, String key, WithdrawalEvent event)
      throws EventPublishingException {
    try {
      String messageId = UUID.randomUUID().toString();
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      String message = event.toJson();

      // Different logging based on event status
      String emoji = getStatusEmoji(event.getStatus());
      String snsMessage = generateSmsMessage(event);

      log.info("{} SNS DEMO MODE: Publishing withdrawal event...", emoji);
      log.info("   Topic ARN: {}", topic);
      log.info("   Message Key: {}", key);
      log.info("   Timestamp: {}", timestamp);
      log.info("   Status: {}", event.getStatus());

      // Simulate network delay if enabled
      if (simulateDelay) {
        try {
          Thread.sleep(100 + (long) (Math.random() * 200)); // 100-300ms delay
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

      // Simulate occasional failures for demo realism
      if (Math.random() < failureRate) {
        throw new RuntimeException("Simulated SNS failure for demo purposes");
      }

      // Simulate successful SNS publish
      log.info("✅ SNS PUBLISH SUCCESS:");
      log.info("   MessageId: {}", messageId);
      log.info("   Event Payload: {}", message);
      log.info("   Message Attributes:");
      log.info("     - accountNumber: {} (String)", key);
      log.info("     - eventType: withdrawal (String)");
      log.info("     - timestamp: {} (String)", timestamp);
      log.info("📱 SMS SIMULATION: \"{}\"", snsMessage);
      log.info("🎯 Event successfully delivered to SNS topic subscribers");

    } catch (Exception e) {
      log.error("❌ SNS DEMO FAILURE: Failed to publish withdrawal event to topic: {} with key: {}",
          topic, key, e);
      throw new EventPublishingException("Demo SNS publishing failed", e);
    }
  }

  /**
   * Gets appropriate emoji for event status.
   */
  private String getStatusEmoji(String status) {
    return switch (status.toUpperCase()) {
      case "SUCCESS" -> "💰";
      case "DECLINED" -> "🚫";
      default -> "📡";
    };
  }

  /**
   * Generates a realistic SMS message based on the withdrawal event.
   */
  private String generateSmsMessage(WithdrawalEvent event) {
    String amount = event.getAmount().toPlainString();
    String accountMasked = "****" + event.getAccountNumber().substring(5);

    return switch (event.getStatus().toUpperCase()) {
      case "SUCCESS" -> String.format(
          "BANK ALERT: Withdrawal of $%s from account %s was SUCCESSFUL. Available balance"
              + " updated.", amount, accountMasked);
      case "DECLINED" -> String.format(
          "BANK ALERT: Withdrawal of $%s from account %s was DECLINED due to insufficient funds.",
          amount, accountMasked);
      default -> String.format(
          "BANK ALERT: Withdrawal transaction of $%s attempted on account %s.",
          amount, accountMasked);
    };
  }

  /**
   * Production mode publishing (placeholder for actual AWS SNS integration).
   */
  private void publishProductionMode(String topic, String key, WithdrawalEvent event)
      throws EventPublishingException {
    try {
      log.info("Publishing withdrawal event to SNS topic: {} with key: {}", topic, key);

      String message = event.toJson();
      log.debug("Event payload: {}", message);

      // TODO: Replace with actual SNS publishing when AWS SDK is added
      // PublishRequest request = PublishRequest.builder()
      //     .topicArn(topic)
      //     .message(message)
      //     .messageAttributes(Map.of(
      //         "accountNumber", MessageAttributeValue.builder()
      //             .dataType("String")
      //             .stringValue(key)
      //             .build(),
      //         "eventType", MessageAttributeValue.builder()
      //             .dataType("String")
      //             .stringValue("withdrawal")
      //             .build(),
      //         "timestamp", MessageAttributeValue.builder()
      //             .dataType("String")
      //             .stringValue(Instant.now().toString())
      //             .build()
      //     ))
      //     .build();

      // PublishResponse response = snsClient.publish(request);
      // log.info("Successfully published event to SNS. MessageId: {}", response.messageId());

      // Fallback logging for production without AWS
      log.warn("⚠️  PRODUCTION MODE: AWS SNS not configured - event logged only");
      log.info("Would publish to SNS - Topic: {}, Key: {}, Message: {}", topic, key, message);

    } catch (Exception e) {
      log.error("Failed to publish withdrawal event to SNS topic: {} with key: {}", topic, key, e);
      throw new EventPublishingException("Failed to publish withdrawal event to SNS", e);
    }
  }
}
