package lms.payment_service_lms.service;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface VNPayService {
    public String createOrder(String userId, String orderInfor, List<String> courseIds, String baseUrl);
    int orderReturn(HttpServletRequest request);
}
