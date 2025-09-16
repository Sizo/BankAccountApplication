package co.za.demo.bankaccountapplication.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a bank account.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @Column(length = 9, nullable = false, unique = true)
  private String accountNumber;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;
}
