package co.za.demo.bankaccountapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Bank Account Application.
 * This class bootstraps the Spring Boot application.
 *
 * @author Sizo Duma
 */
@SpringBootApplication
public class BankAccountApplication {

  /**
   * The main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(BankAccountApplication.class, args);
  }
}
