package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.config.RabbitMQConfig;
import lms.payment_service_lms.dto.OrderDetailDTO;
import lms.payment_service_lms.entity.Order;
import lms.payment_service_lms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final DynamoDbEnhancedClient enhancedClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void addOrder(Order order) {
        DynamoDbTable<Order> orderTable = enhancedClient.table("Order", TableSchema.fromBean(Order.class));
        orderTable.putItem(order);
    }

//    @Override
//    public void updateOrderByTxnRef(String txnRef, int newStatus, String transactionId) {
//        DynamoDbTable<Order> orderTable = enhancedClient.table("Order", TableSchema.fromBean(Order.class));
//
//        // Lấy đơn hàng theo orderId (txnRef)
//        Order existingOrder = orderTable.getItem(r -> r.key(k -> k.partitionValue(txnRef)));
//        String formattedDate = existingOrder.getDate()
//                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//
//        rabbitTemplate.convertAndSend(
//                RabbitMQConfig.EXCHANGE,
//                RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY,
//                new OrderDetailDTO(existingOrder.getUserName(), existingOrder.getUserEmail(), existingOrder.getPrice(), existingOrder.getOrderId(), formattedDate)
//        );
//
//        // Cập nhật các giá trị mới
//        existingOrder.setStatus(newStatus); // ví dụ: 0 = thành công
//        existingOrder.setTransactionId(transactionId); // mã giao dịch thực tế trả về từ VNPay
//
//        // Ghi đè lại đơn hàng
//        orderTable.putItem(existingOrder);
//    }

}
