package co.za.demo.bankaccountapplication.model.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Problem {
  private String type;

  private String title;

  private Integer status;

  private String traceId;

  private String detail;

  private String instance;
}
