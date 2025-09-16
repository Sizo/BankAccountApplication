package co.za.demo.bankaccountapplication.controller;

import co.za.demo.bankaccountapplication.api.BankAccountApi;
import co.za.demo.bankaccountapplication.mapper.WithdrawalMapper;
import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalRequest;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalResponse;
import co.za.demo.bankaccountapplication.service.AccountService;
import co.za.demo.bankaccountapplication.service.TransactionEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling bank account operations such as withdrawals.
 * Implements the generated BankAccountApi interface.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BankAccountController implements BankAccountApi {

  private final AccountService accountService;
  private final WithdrawalMapper withdrawalMapper;
  private final TransactionEventService transactionEventService;

  /**
   * Implementation of the generated API interface method for withdrawal processing.
   *
   * @param withdrawalRequest the withdrawal request payload
   * @param traceId           optional trace ID for tracking requests
   * @return the response entity containing the withdrawal result
   */
  @Override
  public ResponseEntity<WithdrawalResponse> _withdraw(
      WithdrawalRequest withdrawalRequest,
      String traceId) {
    log.info(
        "Withdrawal request received - TraceId: {}, Account: {}, Amount: {}, "
            + "AccountName: {}, AccountType: {}, CustomerId: {}",
        traceId,
        withdrawalRequest.getAccountNumber(),
        withdrawalRequest.getAmount(),
        withdrawalRequest.getAccountName(),
        withdrawalRequest.getAccountType(),
        withdrawalRequest.getCustomerId());

    // Map the request DTO to the domain object
    WithdrawalDo withdrawalDo = withdrawalMapper.toWithdrawalDo(withdrawalRequest);

    // Process the withdrawal using the domain object
    WithdrawalResponse result = accountService.processWithdrawal(withdrawalDo);

    // Publish successful withdrawal event
    transactionEventService.publishWithdrawalEvent(
        withdrawalRequest.getAccountNumber(),
        withdrawalRequest.getAmount(),
        "SUCCESS");

    return ResponseEntity.ok(result);
  }
}