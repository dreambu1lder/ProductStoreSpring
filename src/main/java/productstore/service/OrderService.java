package productstore.service;

import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.model.Order;
import productstore.model.OrderProduct;
import productstore.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    Order saveOrder(Order order);

    void deleteOrderById(Long id);

    OrderProduct addProductToOrder(Order order, Product product, int quantity);
}
