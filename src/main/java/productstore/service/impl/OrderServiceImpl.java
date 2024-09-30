package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.model.Order;
import productstore.model.OrderProduct;
import productstore.model.Product;
import productstore.repository.OrderProductRepository;
import productstore.repository.OrderRepository;
import productstore.service.OrderService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderProductRepository orderProductRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    // Добавление продукта к заказу
    public OrderProduct addProductToOrder(Order order, Product product, int quantity) {
        OrderProduct orderProduct = new OrderProduct(order, product, quantity);
        return orderProductRepository.save(orderProduct);
    }

    // Дополнительные бизнес-методы (если нужны)
}
