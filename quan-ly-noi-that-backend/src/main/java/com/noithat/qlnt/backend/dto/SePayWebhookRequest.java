package com.noithat.qlnt.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SePayWebhookRequest {
    private String id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private BigDecimal transferAmount;
    private String transferType;
    private String description;
    private String referenceCode;
    private String bankBrandName;
}
