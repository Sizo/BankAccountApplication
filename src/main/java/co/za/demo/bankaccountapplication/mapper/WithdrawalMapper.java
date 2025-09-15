package co.za.demo.bankaccountapplication.mapper;

import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import co.za.demo.bankaccountapplication.model.dto.WithdrawalRequest;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between WithdrawalRequest DTO and WithdrawalDo domain object.
 */
@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

  /**
   * Maps WithdrawalRequest DTO to WithdrawalDo domain object.
   *
   * @param request the withdrawal request DTO
   * @return the mapped WithdrawalDo object
   */
  WithdrawalDo toWithdrawalDo(WithdrawalRequest request);
}
