package co.za.demo.bankaccountapplication.mapper;

import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalRequest;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between WithdrawalRequest DTO and WithdrawalDo domain object.
 */
@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

  /**
   * Maps WithdrawalRequest DTO to WithdrawalDo domain object.
   * Converts string amount to BigDecimal for precision preservation.
   *
   * @param request the withdrawal request DTO
   * @return the mapped WithdrawalDo object
   */
  @Mapping(target = "amount", expression = "java(stringToBigDecimal(request.getAmount()))")
  WithdrawalDo toWithdrawalDo(WithdrawalRequest request);

  /**
   * Converts string to BigDecimal for financial precision.
   *
   * @param value the string value to convert
   * @return BigDecimal representation
   */
  default BigDecimal stringToBigDecimal(String value) {
    return value != null ? new BigDecimal(value) : null;
  }

  /**
   * Converts BigDecimal to string for API responses.
   *
   * @param value the BigDecimal value to convert
   * @return string representation
   */
  default String bigDecimalToString(BigDecimal value) {
    return value != null ? value.toPlainString() : null;
  }
}
