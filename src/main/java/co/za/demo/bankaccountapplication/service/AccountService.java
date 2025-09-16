package co.za.demo.bankaccountapplication.service;

import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalResponse;
import co.za.demo.bankaccountapplication.model.entity.Account;
import co.za.demo.bankaccountapplication.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling account-related operations such as withdrawals.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  /**
   * Process a withdrawal using the withdrawal domain object.
   *
   * @param withdrawalDo the withdrawal domain object containing account number and amount
   * @return the withdrawal response with status and new balance
   */
  @Transactional
  public WithdrawalResponse processWithdrawal(WithdrawalDo withdrawalDo) {
    log.info("Processing withdrawal of {} from account {}",
        withdrawalDo.getAmount(), withdrawalDo.getAccountNumber());

    // Find the account by accountNumber
    Account account = accountRepository.findByAccountNumber(withdrawalDo.getAccountNumber())
        .orElseThrow(() -> new EntityNotFoundException(
            "Account not found: " + withdrawalDo.getAccountNumber()));

    BigDecimal newBalance = getAndCalculateNewBalance(withdrawalDo, account);
    account.setBalance(newBalance);
    accountRepository.save(account);

    log.info("Withdrawal successful. New balance for account {}: {}",
        withdrawalDo.getAccountNumber(), newBalance);

    // Create response using proper setter methods (not method chaining)
    var withdrawalResponse = new WithdrawalResponse();
    withdrawalResponse.setAccountNumber(withdrawalDo.getAccountNumber());
    withdrawalResponse.setAmountWithdrawn(withdrawalDo.getAmount().toPlainString());
    withdrawalResponse.setCurrentBalance(newBalance.toPlainString());
    withdrawalResponse.setMessage("SUCCESS");
    return withdrawalResponse;
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
