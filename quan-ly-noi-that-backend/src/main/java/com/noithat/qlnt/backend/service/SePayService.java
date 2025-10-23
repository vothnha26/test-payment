package com.noithat.qlnt.backend.service;

import com.noithat.qlnt.backend.config.SePayConfig;
import com.noithat.qlnt.backend.dto.PaymentRequest;
import com.noithat.qlnt.backend.dto.PaymentResponse;
import com.noithat.qlnt.backend.dto.SePayWebhookRequest;
import com.noithat.qlnt.backend.entity.Payment;
import com.noithat.qlnt.backend.entity.PaymentStatus;
import com.noithat.qlnt.backend.entity.Transaction;
import com.noithat.qlnt.backend.repository.PaymentRepository;
import com.noithat.qlnt.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SePayService {

    private final SePayConfig sePayConfig;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Tạo thanh toán mới với QR code VNPay có số tiền cố định
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            // Tạo mã đơn hàng unique
            String orderId = "ORDER" + System.currentTimeMillis();
            
            // Tạo nội dung chuyển khoản (format: ORDERID hoặc mã tùy chỉnh)
            String content = orderId;
            
            // Tạo Payment entity
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(request.getAmount());
            payment.setDescription(request.getDescription());
            payment.setCustomerName(request.getCustomerName());
            payment.setCustomerEmail(request.getCustomerEmail());
            payment.setCustomerPhone(request.getCustomerPhone());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setExpiredAt(LocalDateTime.now().plusMinutes(15)); // Hết hạn sau 15 phút
            
            // Tạo QR Code VNPay với số tiền cố định
            // Format: bank_id|account_number|account_name|amount|content|template
            String qrData = generateVNPayQRData(
                sePayConfig.getBin(),
                sePayConfig.getAccountNumber(),
                sePayConfig.getAccountName(),
                request.getAmount(),
                content
            );
            
            payment.setQrCode(qrData);
            
            // Lưu vào database
            payment = paymentRepository.save(payment);
            
            log.info("Created payment with orderId: {}, amount: {}", orderId, request.getAmount());
            
            // Tạo response
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(orderId);
            response.setAmount(request.getAmount());
            response.setQrCode(qrData);
            response.setStatus("PENDING");
            response.setMessage("Vui lòng quét mã QR để thanh toán");
            
            return response;
            
        } catch (Exception e) {
            log.error("Error creating payment: ", e);
            throw new RuntimeException("Không thể tạo thanh toán: " + e.getMessage());
        }
    }

    /**
     * Tạo QR Code VNPay với số tiền cố định
     * Format chuẩn VietQR: 00020101021238570010A00000072701270006970436011300065128220208QRIBFTTA53037045405{amount}5802VN62{length}{content}6304{checksum}
     */
    private String generateVNPayQRData(String bin, String accountNumber, String accountName, BigDecimal amount, String content) {
        // VietQR format với amount và content
        // Đơn giản hóa: trả về URL có thể tạo QR với API
        String amountStr = amount.setScale(0).toPlainString();
        
        // URL để tạo QR code (có thể dùng API như QR Code Monkey, hoặc VietQR API)
        String qrUrl = String.format(
            "https://img.vietqr.io/image/%s-%s-compact2.jpg?amount=%s&addInfo=%s&accountName=%s",
            bin, accountNumber, amountStr, content, accountName.replace(" ", "%20")
        );
        
        return qrUrl;
    }

    /**
     * Xử lý webhook từ SePay khi có giao dịch mới
     */
    @Transactional
    public void handleWebhook(SePayWebhookRequest webhook) {
        try {
            log.info("Received webhook: {}", webhook);
            
            // Tìm orderId từ description/content
            String content = webhook.getDescription();
            String orderId = extractOrderId(content);
            
            if (orderId == null) {
                log.warn("Cannot extract orderId from content: {}", content);
                return;
            }
            
            // Tìm payment theo orderId
            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElse(null);
            
            if (payment == null) {
                log.warn("Payment not found for orderId: {}", orderId);
                return;
            }
            
            // Kiểm tra số tiền
            if (webhook.getTransferAmount().compareTo(payment.getAmount()) < 0) {
                log.warn("Amount mismatch. Expected: {}, Received: {}", 
                    payment.getAmount(), webhook.getTransferAmount());
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }
            
            // Cập nhật payment
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            
            // Lưu transaction
            Transaction transaction = new Transaction();
            transaction.setPayment(payment);
            transaction.setTransactionId(webhook.getId());
            transaction.setGateway(webhook.getGateway());
            transaction.setAmount(webhook.getTransferAmount());
            transaction.setContent(webhook.getDescription());
            transaction.setAccountNumber(webhook.getAccountNumber());
            transaction.setSubAccount(webhook.getSubAccount());
            transaction.setBankBrandName(webhook.getBankBrandName());
            
            // Parse transaction date
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                transaction.setTransactionDate(LocalDateTime.parse(webhook.getTransactionDate(), formatter));
            } catch (Exception e) {
                transaction.setTransactionDate(LocalDateTime.now());
            }
            
            transactionRepository.save(transaction);
            
            log.info("Payment completed successfully. OrderId: {}", orderId);
            
        } catch (Exception e) {
            log.error("Error handling webhook: ", e);
        }
    }

    /**
     * Trích xuất orderId từ nội dung chuyển khoản
     */
    private String extractOrderId(String content) {
        if (content == null) return null;
        
        // Tìm pattern ORDERxxxxxxxxx
        if (content.contains("ORDER")) {
            int startIndex = content.indexOf("ORDER");
            String orderId = content.substring(startIndex);
            // Lấy ORDER + 13 số tiếp theo
            if (orderId.length() >= 18) {
                return orderId.substring(0, 18);
            }
            return orderId;
        }
        
        return null;
    }

    /**
     * Kiểm tra trạng thái thanh toán
     */
    public Payment getPaymentStatus(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
    }
}
