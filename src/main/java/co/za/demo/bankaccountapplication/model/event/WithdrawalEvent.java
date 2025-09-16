package co.za.demo.bankaccountapplication.model.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event representing a withdrawal transaction.
 */
@Getter
@AllArgsConstructor
public class WithdrawalEvent {
  private BigDecimal amount;
  private String accountNumber;
  private String status;


  /**
   * Convert to JSON String for publishing.
   *
   * @return JSON representation of the withdrawal event
   */
  public String toJson() {
    return String.format(
        "{\"amount\":\"%s\",\"accountNumber\":\"%s\",\"status\":\"%s\"}",
        amount.toPlainString(),
        accountNumber,
        status);
  }
}
