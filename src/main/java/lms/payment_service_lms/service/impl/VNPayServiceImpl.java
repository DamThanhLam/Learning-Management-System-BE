package lms.payment_service_lms.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lms.payment_service_lms.config.VNPayConfig;
import lms.payment_service_lms.service.VNPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

//    @Override
//    public String createPaymentUrlForVnPay(String orderId, double amount, String returnUrl) throws UnsupportedEncodingException {
//        // Sử dụng HashMap để tạo các tham số và sắp xếp thủ công bằng Collections.sort
//        Map<String, String> vnpParams = new HashMap<>();
//
//        // Thêm các tham số vào vnpParams
//        vnpParams.put("vnp_Version", "2.1.0");
//        vnpParams.put("vnp_Command", "pay");
//        vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
//        vnpParams.put("vnp_Amount", String.valueOf((long) (amount * 100)));  // Số tiền là integer, x100
//        vnpParams.put("vnp_CurrCode", "VND");
//        vnpParams.put("vnp_TxnRef", orderId);
//        vnpParams.put("vnp_OrderInfo", "thanh toan don hang " + orderId); // Không URLEncode ở đây
//        vnpParams.put("vnp_OrderType", "other");
//        vnpParams.put("vnp_Locale", "vn");
//        vnpParams.put("vnp_ReturnUrl", returnUrl); // Không URLEncode ở đây
//        vnpParams.put("vnp_IpAddr", "127.0.0.1");
//        vnpParams.put("vnp_CreateDate", getCurrentDateTime());
//        vnpParams.put("vnp_ExpireDate", getExpireDateTime());
//
//        // Sắp xếp tham số theo thứ tự từ điển (alphabetical order)
//        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
//        Collections.sort(fieldNames);
//
//        // Tạo hashData từ các tham số đã sắp xếp
//        StringBuilder hashData = new StringBuilder();
//        for (String fieldName : fieldNames) {
//            String fieldValue = vnpParams.get(fieldName);
//            if (fieldValue != null && !fieldValue.isEmpty()) {
//                hashData.append(fieldName).append("=").append(fieldValue).append("&");
//            }
//        }
//
//        // Loại bỏ dấu "&" cuối chuỗi
//        if (hashData.length() > 0) {
//            hashData.deleteCharAt(hashData.length() - 1);
//        }
//
//        // Tính toán checksum (HMAC SHA512)
//        String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
//        vnpParams.put("vnp_SecureHash", vnp_SecureHash);
//
//        // Build URL thanh toán
//        StringBuilder paymentUrl = new StringBuilder(VNP_URL);
//        paymentUrl.append("?");
//        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
//            paymentUrl.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
//            paymentUrl.append("=");
//            paymentUrl.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
//            paymentUrl.append("&");
//        }
//
//        // Xoá dấu & cuối chuỗi URL
//        paymentUrl.deleteCharAt(paymentUrl.length() - 1);
//
//        return paymentUrl.toString();
//    }
//
//    // Hàm tạo checksum chuẩn theo yêu cầu VNPAY
//    public String createChecksum(Map<String, String> vnp_Params, String secretKey) {
//        StringBuilder hashData = new StringBuilder();
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//
//        for (String fieldName : fieldNames) {
//            String value = vnp_Params.get(fieldName);
//            if (value != null && !value.isEmpty()) {
//                hashData.append(fieldName).append("=").append(value);
//                hashData.append("&");
//            }
//        }
//        // Loại bỏ dấu "&" cuối
//        if (hashData.length() > 0) {
//            hashData.deleteCharAt(hashData.length() - 1);
//        }
//        return hmacSHA512(secretKey, hashData.toString());
//    }
//
//    // Hàm mã hoá HMAC SHA512
//    public String hmacSHA512(String key, String data) {
//        try {
//            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
//            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            mac.init(secretKey);
//            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            return toHexString(hash);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // Chuyển bytes -> hex
//    public String toHexString(byte[] hash) {
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : hash) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) hexString.append('0');
//            hexString.append(hex);
//        }
//        return hexString.toString().toUpperCase();
//    }
//
//    // Thời gian hiện tại
//    private String getCurrentDateTime() {
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        return formatter.format(calendar.getTime());
//    }
//
//    // Thời gian hết hạn (15 phút sau)
//    private String getExpireDateTime() {
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
//        calendar.add(Calendar.MINUTE, 15);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        return formatter.format(calendar.getTime());
//    }

    @Override
    public String createOrder(int total, String orderInfor, String urlReturn){
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total*100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    @Override
    public int orderReturn(HttpServletRequest request){
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

}
