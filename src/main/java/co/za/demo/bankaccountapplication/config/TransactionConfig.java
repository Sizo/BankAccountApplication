package co.za.demo.bankaccountapplication.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class for transaction management and concurrency control.
 * Provides centralized configuration for database transaction handling with
 * optimistic locking and proper isolation levels.
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

  /**
   * Configure JPA transaction manager with enhanced settings for concurrency control.
   * This transaction manager handles optimistic locking exceptions and ensures
   * proper transaction isolation for concurrent operations.
   *
   * @param entityManagerFactory the JPA entity manager factory
   * @return configured PlatformTransactionManager
   */
  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);

    // Enable validation of existing transactions
    transactionManager.setValidateExistingTransaction(true);

    // Fail early on transaction timeout
    transactionManager.setFailEarlyOnGlobalRollbackOnly(true);

    // Enable JTA synchronization for better transaction coordination
    transactionManager.setGlobalRollbackOnParticipationFailure(true);

    return transactionManager;
  }
}
