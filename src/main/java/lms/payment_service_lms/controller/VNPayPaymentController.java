package lms.payment_service_lms.controller;

import jakarta.servlet.http.HttpServletRequest;
import lms.payment_service_lms.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vnpay")
public class VNPayPaymentController {

    private final VNPayService vnPayService;

    // API để tạo URL thanh toán VNPAY
//    @GetMapping("/payment")
//    public String createPaymentUrl(@RequestParam("orderId") String orderId,
//                                   @RequestParam("amount") double amount,
//                                   @RequestParam("returnUrl") String returnUrl) {
//        try {
//            // Kiểm tra URL trả về, nếu không hợp lệ thì gán giá trị mặc định
//            if (returnUrl == null || returnUrl.isEmpty()) {
//                returnUrl = "http://localhost:8080/api/v1/vnpay/return";  // Địa chỉ URL callback của bạn
//            }
//
//            // Gọi hàm tạo URL thanh toán VNPAY
//            String paymentUrl = vnPayService.createPaymentUrlForVnPay(orderId, amount, returnUrl);
//
//            return "Chuyển hướng tới URL thanh toán VNPAY: <a href=\"" + paymentUrl + "\">" + paymentUrl + "</a>";
//        } catch (UnsupportedEncodingException e) {
//            return "Lỗi khi tạo URL thanh toán: " + e.getMessage();
//        }
//    }

    // API để xử lý phản hồi từ VNPAY sau khi thanh toán
//    @GetMapping("/return")
//    public String handlePaymentResponse(HttpServletRequest request) {
//        try {
//            // Lấy tất cả các tham số từ phản hồi của VNPAY
//            Map<String, String[]> parameterMap = request.getParameterMap();
//
//            // Tạo chuỗi tham số để kiểm tra checksum
//            StringBuilder vnpResponse = new StringBuilder();
//            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//                String paramName = entry.getKey();
//                String paramValue = entry.getValue()[0];
//                if (paramValue != null && !paramValue.isEmpty() && !paramName.equals("vnp_SecureHash")) {
//                    vnpResponse.append(paramName).append("=").append(paramValue).append("&");
//                }
//            }
//
//            // Loại bỏ dấu '&' cuối cùng nếu có
//            if (vnpResponse.length() > 0) {
//                vnpResponse.deleteCharAt(vnpResponse.length() - 1);
//            }
//
//            // Tạo checksum từ tham số phản hồi và so sánh với giá trị checksum trong phản hồi
//            String secureHash = request.getParameter("vnp_SecureHash");
//            String checkSum = vnPayService.createChecksum(vnpResponse.toString());
//
//            if (secureHash != null && secureHash.equals(checkSum)) {
//                // Kiểm tra trạng thái thanh toán
//                String vnpTransactionStatus = request.getParameter("vnp_ResponseCode");
//                String orderId = request.getParameter("vnp_TxnRef");
//
//                if ("00".equals(vnpTransactionStatus)) {
//                    // Thanh toán thành công
//                    return "Thanh toán thành công cho đơn hàng " + orderId;
//                } else {
//                    // Thanh toán thất bại
//                    return "Thanh toán thất bại cho đơn hàng " + orderId;
//                }
//            } else {
//                // Checksum không hợp lệ
//                return "Lỗi bảo mật, dữ liệu bị thay đổi.";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Lỗi xử lý phản hồi từ VNPAY: " + e.getMessage();
//        }
//    }


    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request){
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<Map<String, String>> GetMapping(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, String> response = new HashMap<>();
        response.put("paymentStatus", String.valueOf(paymentStatus));
        response.put("orderInfo", orderInfo != null ? orderInfo : "");
        response.put("paymentTime", paymentTime != null ? paymentTime : "");
        response.put("transactionId", transactionId != null ? transactionId : "");
        response.put("totalPrice", totalPrice != null ? totalPrice : "");

        return ResponseEntity.ok(response);
    }
}
