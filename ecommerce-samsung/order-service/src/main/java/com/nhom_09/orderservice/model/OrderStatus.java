package com.nhom_09.orderservice.model;

public enum OrderStatus {
    PENDING,        // Chờ xử lý kho
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    STOCK_RESERVED, // Đã giữ kho, chờ thanh toán

    STOCK_FAILED,
    SUCCESS,        // Hoàn thành
    CANCELLED       // Bị hủy (do hết hàng, thanh toán lỗi, timeout...)
}
