package co.za.demo.bankaccountapplication.model.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {
  private String accountNumber;
  private BigDecimal amountWithdrawn;
  private BigDecimal currentBalance;
  private String message;

  /**
   * Constructor for simple status and balance response.
   *
   * @param status the status of the withdrawal
   * @param newBalance the new balance after withdrawal
   */
  public WithdrawalResponse(String status, BigDecimal newBalance) {
    this.message = status;
    this.currentBalance = newBalance;
  }
}
