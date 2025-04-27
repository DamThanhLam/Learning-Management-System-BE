package lms.payment_service_lms.service;

import lms.payment_service_lms.entity.Order;

import java.util.Optional;

public interface OrderService {
    public void addOrder(Order orderNew);
//    public void updateOrderByTxnRef(String txnRef, int newStatus, String transactionId);
}
