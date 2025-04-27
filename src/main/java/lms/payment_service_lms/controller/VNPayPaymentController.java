package lms.payment_service_lms.controller;

import jakarta.servlet.http.HttpServletRequest;
import lms.payment_service_lms.config.RabbitMQConfig;
import lms.payment_service_lms.dto.OrderDetailDTO;
import lms.payment_service_lms.entity.Order;
import lms.payment_service_lms.entity.OrderHistory;
import lms.payment_service_lms.service.OrderHistoryService;
import lms.payment_service_lms.service.OrderService;
import lms.payment_service_lms.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vnpay")
public class VNPayPaymentController {

    private final VNPayService vnPayService;
    private final OrderHistoryService orderHistoryService;
    private final RabbitTemplate rabbitTemplate;
    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("userId") String userId,
                              @RequestParam("courseIds") List<String> courseIds,
                              @RequestParam(value = "orderInfo", defaultValue = "No") String orderInfo,
                              HttpServletRequest request){
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return vnPayService.createOrder(userId, orderInfo, courseIds, baseUrl);
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<Map<String, String>> handleVNPayReturn(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            // Lấy mã giao dịch từ VNPay
            String txnRef = Optional.ofNullable(request.getParameter("vnp_TxnRef")).orElse("");
            String transactionId = Optional.ofNullable(request.getParameter("vnp_TransactionNo")).orElse("");
            String orderInfo = Optional.ofNullable(request.getParameter("vnp_OrderInfo")).orElse("");
            String paymentTime = Optional.ofNullable(request.getParameter("vnp_PayDate")).orElse("");
            String totalPrice = Optional.ofNullable(request.getParameter("vnp_Amount")).orElse("");

            if (txnRef.isBlank()) {
                response.put("error", "Thiếu mã giao dịch (TxnRef)");
                return ResponseEntity.badRequest().body(response);
            }

            int paymentStatus = vnPayService.orderReturn(request);

            // Cập nhật đơn hàng
            orderHistoryService.updateOrderHistoryByTxnRef(txnRef, paymentStatus, transactionId);

            // Trả thông tin phản hồi
            response.put("paymentStatus", String.valueOf(paymentStatus));
            response.put("orderInfo", orderInfo);
            response.put("paymentTime", paymentTime);
            response.put("transactionId", transactionId);
            response.put("totalPrice", totalPrice);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Có lỗi xảy ra trong quá trình xử lý VNPay: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
