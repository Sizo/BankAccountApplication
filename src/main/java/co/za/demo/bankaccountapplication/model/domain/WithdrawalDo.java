package co.za.demo.bankaccountapplication.model.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * A class representing the domain object for a withdrawal operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDo {

  @NotNull
  @Length(min = 9, max = 9)
  private String accountNumber;

  @NotNull
  @DecimalMin("0")
  private BigDecimal amount;

  // Optional fields for audit and logging purposes
  private String accountName;
  private String accountType;
  private String customerId;
}
