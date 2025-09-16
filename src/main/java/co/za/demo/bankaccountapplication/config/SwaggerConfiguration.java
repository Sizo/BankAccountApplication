package co.za.demo.bankaccountapplication.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI and OpenAPI configuration.
 * Provides interactive API documentation with enhanced metadata.
 */
@Configuration
public class SwaggerConfiguration {

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${app.version:0.0.1-SNAPSHOT}")
  private String version;

  @Value("${server.port:8080}")
  private String serverPort;

  /**
   * Configure OpenAPI documentation with comprehensive metadata.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title(applicationName + " API")
            .version(version)
            .description("""
                Enterprise Banking Application demonstrating modern Spring Boot 3.5.5 architecture
                with comprehensive fault tolerance, observability, and business rule validation.
                
                ## Features
                - **Fault Tolerance**: Circuit breakers, retries, rate limiting, and bulkhead isolation
                - **API-First Design**: OpenAPI specification drives implementation
                - **Production Ready**: Comprehensive monitoring and observability
                """)
            .contact(new Contact()
                .name("Bank Development Team")
                .email("dev-team@bankapp.com")
                .url("https://github.com/bankapp/bank-account-application"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Development Server")
        ));
  }
}
