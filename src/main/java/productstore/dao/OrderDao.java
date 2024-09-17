package productstore.dao;

import productstore.model.Order;
import productstore.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {

    Order saveOrder(Order order) throws SQLException;
    Order getOrderById(long id) throws SQLException;
    List<Order> getAllOrders() throws SQLException;
    void updateOrder(Order order) throws SQLException;
    void deleteOrder(long id) throws SQLException;
    void addProductsToOrder(long orderId, List<Product> products) throws SQLException;
    List<Product> getProductsByOrderId(long orderId) throws SQLException;
    List<Order> getOrdersWithPagination(int pageNumber, int pageSize) throws SQLException;
}
