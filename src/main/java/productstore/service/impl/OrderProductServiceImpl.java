package productstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.model.OrderProduct;
import productstore.model.OrderProductPK;
import productstore.repository.OrderProductRepository;
import productstore.service.OrderProductService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderProductServiceImpl implements OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProductServiceImpl(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    public List<OrderProduct> getAllOrderProducts() {
        return orderProductRepository.findAll();
    }

    public Optional<OrderProduct> getOrderProductById(OrderProductPK id) {
        return orderProductRepository.findById(id);
    }

    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }

    public void deleteOrderProduct(OrderProductPK id) {
        orderProductRepository.deleteById(id);
    }

    // Дополнительные бизнес-методы (если нужны)
}
