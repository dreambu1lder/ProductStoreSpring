package productstore.service;

import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.sql.SQLException;
import java.util.List;

public interface OrderService {
    OrderOutputDTO createOrder(OrderInputDTO orderDto) throws SQLException;
    OrderOutputDTO getOrderById(long id) throws SQLException;
    List<OrderOutputDTO> getAllOrders() throws SQLException;
    void addProductsToOrder(long orderId, List<Long> productIds) throws SQLException;
    List<ProductOutputDTO> getProductsByOrderId(long orderId) throws SQLException;
}
