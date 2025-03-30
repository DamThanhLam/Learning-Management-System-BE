package lms.payment_service_lms.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface VNPayService {
    String createPaymentUrlForVnPay(String orderId, double amount, String returnUrl) throws UnsupportedEncodingException;
    String createChecksum(Map<String, String> vnpParams);
    String toHexString(byte[] hash);
}
