package co.za.demo.bankaccountapplication.mapper;

import co.za.demo.bankaccountapplication.model.domain.WithdrawalDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    WithdrawalMapper INSTANCE = Mappers.getMapper(WithdrawalMapper.class);

    /**
     * Maps controller parameters to the WithdrawalDo domain object.
     *
     * @param accountId the account ID from the path variable
     * @param amount the amount as a string from the request parameter
     * @return the mapped WithdrawalDo object
     */
    @Mapping(source = "accountId", target = "accountNumber")
    @Mapping(target = "amount", expression = "java(convertStringToBigDecimal(amount))")
    WithdrawalDo toWithdrawalDo(String accountId, String amount);

    /**
     * Converts a string amount to BigDecimal.
     *
     * @param amount the amount as a string
     * @return the converted BigDecimal amount
     */
    default BigDecimal convertStringToBigDecimal(String amount) {
        if (amount == null || amount.isEmpty()) {
            return null;
        }
        return new BigDecimal(amount);
    }
}
