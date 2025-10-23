package com.noithat.qlnt.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private BigDecimal amount;
    private String qrCode;
    private String paymentUrl;
    private String status;
    private String message;
}
