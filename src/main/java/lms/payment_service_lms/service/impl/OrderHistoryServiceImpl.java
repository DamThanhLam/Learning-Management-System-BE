package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.config.RabbitMQConfig;
import lms.payment_service_lms.dto.OrderDetailDTO;
import lms.payment_service_lms.entity.Order;
import lms.payment_service_lms.entity.OrderHistory;
import lms.payment_service_lms.repositories.OrderHistoryRepository;
import lms.payment_service_lms.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void createOrderHistory(OrderHistory orderHistory) {
        orderHistoryRepository.create(orderHistory);
    }

    @Override
    public Optional<OrderHistory> getOrderHistory(String orderId) {
        return Optional.ofNullable(orderHistoryRepository.findById(orderId));
    }

    @Override
    public void updateOrderHistoryByTxnRef(String txnRef, int newStatus, String transactionId) {

        // Lấy đơn hàng theo orderId (txnRef)
        OrderHistory existingOrder = orderHistoryRepository.findById(txnRef);
        String formattedDate = existingOrder.getDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY,
                new OrderDetailDTO(existingOrder.getUserName(), existingOrder.getUserEmail(), existingOrder.getTotal(), existingOrder.getId(), formattedDate)
        );

        // Cập nhật các giá trị mới
        existingOrder.setStatus(newStatus); // ví dụ: 0 = thành công
        existingOrder.setTransactionId(transactionId); // mã giao dịch thực tế trả về từ VNPay

        // Ghi đè lại đơn hàng
        orderHistoryRepository.create(existingOrder);
    }
}
