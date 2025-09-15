package co.za.demo.bankaccountapplication.controller;


import co.za.demo.bankaccountapplication.mapper.WithdrawalMapper;
import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalRequest;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalResponse;
import co.za.demo.bankaccountapplication.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling bank account operations such as withdrawals.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/banks")
@RequiredArgsConstructor
public class BankAccountController {

  private final AccountService accountService;
  private final WithdrawalMapper withdrawalMapper;

  /**
   * Endpoint to handle withdrawal requests.
   *
   * @param request the withdrawal request payload
   * @param traceId optional trace ID for tracking requests
   * @return the response entity containing the withdrawal result
   */
  @PostMapping("/withdrawals")
  public ResponseEntity<WithdrawalResponse> withdraw(
      @Valid @RequestBody WithdrawalRequest request,
      @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

    log.info("Withdrawal request received - TraceId: {}, Account: {}, Amount: {}, AccountName: {}, AccountType: {}, CustomerId: {}", 
        traceId, request.getAccountNumber(), request.getAmount(), 
        request.getAccountName(), request.getAccountType(), request.getCustomerId());

    // Map the request DTO to the domain object
    WithdrawalDo withdrawalDo = withdrawalMapper.toWithdrawalDo(request);

    // Process the withdrawal using the domain object
    WithdrawalResponse result = accountService.processWithdrawal(withdrawalDo);
    return ResponseEntity.ok(result);
  }
}