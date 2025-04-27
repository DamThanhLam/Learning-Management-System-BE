package lms.payment_service_lms.service;

import lms.payment_service_lms.entity.OrderHistory;

import java.util.Optional;

public interface OrderHistoryService {
    void createOrderHistory(OrderHistory orderHistory);
    Optional<OrderHistory> getOrderHistory(String orderId);
    void updateOrderHistoryByTxnRef(String txnRef, int newStatus, String transactionId);
}
