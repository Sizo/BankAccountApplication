package co.za.demo.bankaccountapplication.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity representing a bank account with optimistic locking for concurrency control.
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

  /**
   * Version field for optimistic locking to handle concurrent transactions.
   * This ensures that if two transactions try to update the same account simultaneously,
   * only one will succeed and the other will receive an OptimisticLockException.
   */
  @Version
  @Column(nullable = false)
  private Long version;

  /**
   * Timestamp when the account was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp when the account was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
