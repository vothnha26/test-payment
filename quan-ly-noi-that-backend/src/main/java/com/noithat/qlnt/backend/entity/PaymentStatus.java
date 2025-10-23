package com.noithat.qlnt.backend.entity;

public enum PaymentStatus {
    PENDING,      // Đang chờ thanh toán
    PROCESSING,   // Đang xử lý
    COMPLETED,    // Hoàn thành
    FAILED,       // Thất bại
    CANCELLED,    // Đã hủy
    REFUNDED      // Đã hoàn tiền
}
