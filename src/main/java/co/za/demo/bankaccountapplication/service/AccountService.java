package co.za.demo.bankaccountapplication.service;

import co.za.demo.bankaccountapplication.exception.ServiceTemporarilyUnavailableException;
import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalResponse;
import co.za.demo.bankaccountapplication.model.entity.Account;
import co.za.demo.bankaccountapplication.repository.AccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling account-related operations such as withdrawals.
 * Enhanced with fault tolerance mechanisms and optimistic locking for concurrency control.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  /**
   * Process a withdrawal using the withdrawal domain object.
   * Enhanced with multiple fault tolerance patterns and optimistic locking for concurrency:
   * - Retry with exponential backoff for transient failures and concurrent updates
   * - Circuit breaker to prevent cascade failures
   * - Rate limiting to prevent system overload
   * - Optimistic locking to handle concurrent account updates safely
   *
   * @param withdrawalDo the withdrawal domain object containing account number and amount
   * @return the withdrawal response with status and new balance
   */
  @Transactional(
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.REQUIRED,
      timeout = 30,
      rollbackFor = {Exception.class}
  )
  @Retry(name = "accountService", fallbackMethod = "fallbackProcessWithdrawal")
  @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackProcessWithdrawal")
  @RateLimiter(name = "accountService")
  @Retryable(
      retryFor = {OptimisticLockException.class, Exception.class},
      noRetryFor = {IllegalArgumentException.class, EntityNotFoundException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 500, multiplier = 2.0, maxDelay = 2000)
  )
  public WithdrawalResponse processWithdrawal(WithdrawalDo withdrawalDo) {
    log.info("Processing withdrawal of {} from account {}",
        withdrawalDo.getAmount(), withdrawalDo.getAccountNumber());

    try {
      Account account = findAccountWithRetry(withdrawalDo.getAccountNumber());
      BigDecimal newBalance = getAndCalculateNewBalance(withdrawalDo, account);

      // Update balance and save - optimistic locking will handle concurrent updates
      account.setBalance(newBalance);
      Account savedAccount = accountRepository.save(account);

      log.info("Withdrawal successful. New balance for account {}: {}, Version: {}",
          withdrawalDo.getAccountNumber(), newBalance, savedAccount.getVersion());

      // Create response using proper setter methods
      var withdrawalResponse = new WithdrawalResponse();
      withdrawalResponse.setAccountNumber(withdrawalDo.getAccountNumber());
      withdrawalResponse.setAmountWithdrawn(withdrawalDo.getAmount().toPlainString());
      withdrawalResponse.setCurrentBalance(newBalance.toPlainString());
      withdrawalResponse.setMessage("SUCCESS");
      return withdrawalResponse;

    } catch (IllegalArgumentException | EntityNotFoundException ex) {
      // Business validation errors should propagate to GlobalExceptionHandler for proper
      // HTTP status codes
      log.warn("Business validation error for account {}: {}",
          withdrawalDo.getAccountNumber(), ex.getMessage());
      throw ex;
    } catch (OptimisticLockException ex) {
      log.warn(
          "Optimistic lock exception for account {}. Concurrent update detected. Attempt will be "
              + "retried.",
          withdrawalDo.getAccountNumber());
      throw ex; // Let retry mechanism handle this
    }
  }

  /**
   * Find account with retry mechanism for database operations.
   * Uses pessimistic read to ensure data consistency during concurrent operations.
   *
   * @param accountNumber the account number to find
   * @return the account entity
   */
  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  @Retry(name = "database-operations")
  @Retryable(
      retryFor = {Exception.class},
      noRetryFor = {EntityNotFoundException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 200, multiplier = 1.5)
  )
  public Account findAccountWithRetry(String accountNumber) {
    log.debug("Attempting to find account: {}", accountNumber);
    return accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new EntityNotFoundException(
            "Account not found: " + accountNumber));
  }

  /**
   * Process withdrawal with explicit concurrency handling for high-contention scenarios.
   * This method includes additional logic for handling multiple concurrent withdrawal attempts.
   *
   * @param withdrawalDo the withdrawal domain object
   * @return the withdrawal response
   */
  @Transactional(
      isolation = Isolation.REPEATABLE_READ,
      propagation = Propagation.REQUIRES_NEW,
      timeout = 45
  )
  @Retryable(
      retryFor = {OptimisticLockException.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 100, multiplier = 1.2, maxDelay = 1000)
  )
  public WithdrawalResponse processWithdrawalWithConcurrencyControl(WithdrawalDo withdrawalDo) {
    log.debug("Processing withdrawal with enhanced concurrency control for account: {}",
        withdrawalDo.getAccountNumber());

    // Delegate to main processing method
    return processWithdrawal(withdrawalDo);
  }

  /**
   * Fallback method for withdrawal processing when all retries fail or circuit breaker is open.
   * This should only handle infrastructure failures, not business validation errors.
   *
   * @param withdrawalDo the withdrawal domain object
   * @param ex           the exception that triggered the fallback
   * @return a fallback withdrawal response
   */
  public WithdrawalResponse fallbackProcessWithdrawal(WithdrawalDo withdrawalDo, Exception ex) {
    // Business validation errors should never reach fallback - they should propagate to
    // GlobalExceptionHandler
    if (ex instanceof IllegalArgumentException || ex instanceof EntityNotFoundException) {
      log.error("Business validation error unexpectedly reached fallback for account {}: {}",
          withdrawalDo.getAccountNumber(), ex.getMessage());
      throw (RuntimeException) ex; // Re-throw to reach GlobalExceptionHandler
    }

    if (ex instanceof OptimisticLockException) {
      log.error("Fallback triggered due to persistent optimistic lock conflicts for account {}. "
          + "High concurrency detected.", withdrawalDo.getAccountNumber());
    } else {
      log.error("Fallback triggered for withdrawal from account {}. Reason: {}",
          withdrawalDo.getAccountNumber(), ex.getMessage());
    }

    var fallbackResponse = new WithdrawalResponse();
    fallbackResponse.setAccountNumber(withdrawalDo.getAccountNumber());
    fallbackResponse.setAmountWithdrawn("0.00");
    fallbackResponse.setCurrentBalance("0.00");

    if (ex instanceof OptimisticLockException) {
      fallbackResponse.setMessage("CONCURRENT_UPDATE_DETECTED - Please try again");
    } else {
      fallbackResponse.setMessage("TEMPORARILY_UNAVAILABLE - Please try again later");
    }

    return fallbackResponse;
  }

  private static BigDecimal getAndCalculateNewBalance(WithdrawalDo withdrawalDo, Account account) {
    BigDecimal currentBalance = account.getBalance();

    // Check if sufficient funds are available
    if (currentBalance.compareTo(withdrawalDo.getAmount()) < 0) {
      throw new IllegalArgumentException(
          "Insufficient funds in account: "
              + withdrawalDo.getAccountNumber()
              + ". Current balance: " + currentBalance
              + ", Requested amount: " + withdrawalDo.getAmount());
    }

    // Calculate new balance and update the account
    return currentBalance.subtract(withdrawalDo.getAmount());
  }
}
