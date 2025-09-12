package co.za.demo.bankaccountapplication.model.domain;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
}
