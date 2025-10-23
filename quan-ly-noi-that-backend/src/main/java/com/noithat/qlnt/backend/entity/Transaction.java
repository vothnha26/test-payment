package com.noithat.qlnt.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "gateway")
    private String gateway; // SePay
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "content")
    private String content;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "sub_account")
    private String subAccount;
    
    @Column(name = "bank_brand_name")
    private String bankBrandName;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
