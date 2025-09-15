package co.za.demo.bankaccountapplication.repository;

import co.za.demo.bankaccountapplication.model.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
  /**
   * Finds an account by its account number.
   *
   * @param accountNumber the account number to search for
   * @return an Optional containing the found Account, or empty if not found
   */
  Optional<Account> findByAccountNumber(String accountNumber);
}
