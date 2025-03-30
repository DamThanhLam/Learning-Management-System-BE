package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.service.VNPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    @Value("${vnp.tmnCode}")
    private String VNP_TMN_CODE;

    @Value("${vnp.hashSecret}")
    private String VNP_HASH_SECRET;

    @Value("${vnp.url}")
    private String VNP_URL;

    @Override
    public String createPaymentUrlForVnPay(String orderId, double amount, String returnUrl) throws UnsupportedEncodingException {
        // Sử dụng TreeMap để tự động sắp xếp tham số theo thứ tự từ điển
        Map<String, String> vnpParams = new TreeMap<>();

        vnpParams.put("vnp_Version", "2.1.0");  // Phiên bản API
        vnpParams.put("vnp_Command", "pay");  // Thêm tham số Command
        vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf((int) (amount * 100)));  // Chuyển đổi sang đơn vị VND
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId);  // Mã đơn hàng
        vnpParams.put("vnp_OrderInfo", URLEncoder.encode("thanh toan don hang " + orderId, "UTF-8"));
        vnpParams.put("vnp_IpAddr", "127.0.0.1");
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");  // Ngôn ngữ

        // URL callback trả kết quả thanh toán
        vnpParams.put("vnp_ReturnUrl", URLEncoder.encode(returnUrl, "UTF-8"));

        vnpParams.put("vnp_CreateDate", getCurrentDateTime());  // Thời gian hiện tại
        vnpParams.put("vnp_ExpireDate", getExpireDateTime());  // Thời gian hết hạn sau 15 phút

        // Tạo checksum
        String checkSum = createChecksum(vnpParams);

        // Thêm tham số checksum vào URL
        vnpParams.put("vnp_SecureHash", checkSum);

        // Xây dựng URL thanh toán
        StringBuilder paymentUrl = new StringBuilder(VNP_URL);
        paymentUrl.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            paymentUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // Loại bỏ dấu "&" ở cuối chuỗi URL
        paymentUrl.deleteCharAt(paymentUrl.length() - 1);

        return paymentUrl.toString();
    }

    // Hàm tạo checksum
    public String createChecksum(Map<String, String> vnpParams) {
        StringBuilder signData = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // Loại bỏ dấu "&" ở cuối chuỗi
        signData.deleteCharAt(signData.length() - 1);
        signData.append("&vnp_HashSecret=").append(VNP_HASH_SECRET);

        return toHexString(hmacSHA512(signData.toString(), VNP_HASH_SECRET));  // Sử dụng HMACSHA512
    }

    // Hàm tính HMACSHA512
    public byte[] hmacSHA512(String data, String key) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");  // Sử dụng HMACSHA512
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
            mac.init(secretKey);
            return mac.doFinal(data.getBytes());  // Tính toán HMAC
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hàm chuyển đổi byte array thành chuỗi hexadecimal
    public String toHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(calendar.getTime());
    }

    // Hàm lấy thời gian hết hạn giao dịch (15 phút sau)
    private String getExpireDateTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        calendar.add(Calendar.MINUTE, 15);  // Thêm 15 phút cho thời gian hết hạn
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(calendar.getTime());
    }
}
