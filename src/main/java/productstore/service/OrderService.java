package productstore.service;

import productstore.servlet.dto.OrderDTO;
import productstore.servlet.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDto) throws SQLException;
    OrderDTO getOrderById(long id) throws SQLException;
    List<OrderDTO> getAllOrders() throws SQLException;
    void addProductsToOrder(long orderId, List<Long> productIds) throws SQLException;
    List<ProductDTO> getProductsByOrderId(long orderId) throws SQLException;
}
