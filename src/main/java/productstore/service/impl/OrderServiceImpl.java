package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.mapper.OrderMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.repository.OrderRepository;
import productstore.service.OrderService;
import productstore.service.exception.OrderNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public List<OrderOutputDTO> getAllOrders() {
        return orderMapper.toDTOs(orderRepository.findAll());
    }

    @Transactional(readOnly = true)
    public OrderOutputDTO getOrderById(Long id) {
        return orderMapper.toDTO(orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found.")));
    }

    @Transactional
    public OrderOutputDTO saveOrder(OrderInputDTO orderInputDTO) {
        return orderMapper.toDTO(orderRepository.save(orderMapper.toEntity(orderInputDTO)));
    }

    @Transactional
    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        // Разорвать связи с продуктами
        order.getOrderProducts().clear();
        orderRepository.save(order);

        // Удалить заказ
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderOutputDTO updateOrderById(Long id, ProductIdsDTO productIdsDTO) {
       Order order = orderRepository.findById(id)
               .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found."));
       List<Product> products2 = orderMapper.mapProductIdsToProducts(productIdsDTO.getProductIds());
       List<Product> products1 = order.getOrderProducts();
       List<Product> orderProducts = Stream.of(products1, products2)
                       .flatMap(List::stream)
                               .toList();
       order.setOrderProducts(orderProducts);
       return orderMapper.toDTO(orderRepository.save(order));
    }
}
