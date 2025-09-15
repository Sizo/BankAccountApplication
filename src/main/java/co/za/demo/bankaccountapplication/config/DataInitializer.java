package co.za.demo.bankaccountapplication.config;

import co.za.demo.bankaccountapplication.model.entity.Account;
import co.za.demo.bankaccountapplication.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DataInitializer is responsible for initializing the H2 database with sample accounts
 * when the application starts up.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

  private final AccountRepository accountRepository;

  /**
   * Initialize the H2 database with sample accounts at application startup.
   *
   * @return a CommandLineRunner that initializes the database
   */
  @Bean
  public CommandLineRunner initDatabase() {
    return args -> {
      log.info("Initializing H2 database with sample accounts...");

      // Only initialize if database is empty
      if (accountRepository.count() == 0) {
        List<Account> accounts = List.of(
            Account.builder()
                .accountNumber("123456789")
                .balance(new BigDecimal("7670.00"))
                .build(),
            Account.builder()
                .accountNumber("987654321")
                .balance(new BigDecimal("20000.00"))
                .build(),
            Account.builder()
                .accountNumber("012345678")
                .balance(new BigDecimal("1.00"))
                .build(),
            Account.builder()
                .accountNumber("087654321")
                .balance(new BigDecimal("9000000.00"))
                .build()
        );

        accountRepository.saveAll(accounts);
        log.info("Successfully created {} test accounts", accounts.size());

        accounts.forEach(account ->
            log.info("Account: {} - Balance: R{}",
                account.getAccountNumber(), account.getBalance())
        );
      } else {
        log.info("Database already contains {} accounts, skipping initialization",
            accountRepository.count());
      }
    };
  }
}
