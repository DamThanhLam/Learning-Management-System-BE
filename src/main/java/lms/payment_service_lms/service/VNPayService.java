package lms.payment_service_lms.service;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface VNPayService {
    String createOrder(int total, String orderInfor, String urlReturn);
    int orderReturn(HttpServletRequest request);
}
