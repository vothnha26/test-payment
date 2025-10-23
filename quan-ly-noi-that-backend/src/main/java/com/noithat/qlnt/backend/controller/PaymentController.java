package com.noithat.qlnt.backend.controller;

import com.noithat.qlnt.backend.dto.PaymentRequest;
import com.noithat.qlnt.backend.dto.PaymentResponse;
import com.noithat.qlnt.backend.dto.SePayWebhookRequest;
import com.noithat.qlnt.backend.entity.Payment;
import com.noithat.qlnt.backend.service.SePayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final SePayService sePayService;

    /**
     * Tạo thanh toán mới
     * POST /api/payment/create
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        log.info("Creating payment: {}", request);
        PaymentResponse response = sePayService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra trạng thái thanh toán
     * GET /api/payment/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<Payment> getPaymentStatus(@PathVariable String orderId) {
        log.info("Checking payment status for orderId: {}", orderId);
        Payment payment = sePayService.getPaymentStatus(orderId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Webhook từ SePay
     * POST /api/payment/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody SePayWebhookRequest webhook) {
        log.info("Received webhook: {}", webhook);
        sePayService.handleWebhook(webhook);
        return ResponseEntity.ok("OK");
    }

    /**
     * Giả lập webhook để test (chỉ dùng khi develop)
     * POST /api/payment/test-webhook/{orderId}
     */
    @PostMapping("/test-webhook/{orderId}")
    public ResponseEntity<String> testWebhook(@PathVariable String orderId) {
        log.info("Testing webhook for orderId: {}", orderId);
        
        // Tạo webhook giả
        SePayWebhookRequest webhook = new SePayWebhookRequest();
        webhook.setId("TEST" + System.currentTimeMillis());
        webhook.setGateway("SePay");
        webhook.setTransactionDate(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        webhook.setAccountNumber("0123456789");
        webhook.setSubAccount("ABC");
        webhook.setDescription("Thanh toan " + orderId);
        webhook.setBankBrandName("VietQR");
        
        // Lấy số tiền từ payment
        Payment payment = sePayService.getPaymentStatus(orderId);
        webhook.setTransferAmount(payment.getAmount());
        
        sePayService.handleWebhook(webhook);
        
        return ResponseEntity.ok("Webhook test completed. Check payment status!");
    }
}
