package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.repositories.OrderDetailRepository;
import fit.iuh.edu.com.services.BL.OrderDetailBL;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailImpl implements OrderDetailBL {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public boolean checkCoursePurchased(String courseId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderDetailRepository.getOrderDetailByCourseIdAndUserId(courseId, userId) != null;
    }
}
