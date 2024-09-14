package productstore.dao.impl;

import productstore.dao.OrderDao;
import productstore.dao.SqlQueries;
import productstore.dao.util.DaoUtils;
import productstore.dao.util.PreparedStatementSetter;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;
import productstore.db.DataBaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order saveOrder(Order order) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection()) {
            return DaoUtils.executeInTransaction(connection, () -> {
                String insertOrderSql = SqlQueries.INSERT_INTO.getSql().formatted("orders", "user_id", "?");
                String insertOrderProductsSql = SqlQueries.INSERT_INTO.getSql().formatted("orders_products", "order_id, product_id", "?, ?");

                try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement insertOrderProductsStmt = connection.prepareStatement(insertOrderProductsSql)) {

                    insertOrderStmt.setLong(1, order.getUser().getId());
                    insertOrderStmt.executeUpdate();

                    try (ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long generatedOrderId = generatedKeys.getLong(1);
                            order.setId(generatedOrderId);

                            for (Product product : order.getProducts()) {
                                insertOrderProductsStmt.setLong(1, generatedOrderId);
                                insertOrderProductsStmt.setLong(2, product.getId());
                                insertOrderProductsStmt.addBatch();
                            }
                            insertOrderProductsStmt.executeBatch();
                        } else {
                            throw new SQLException("Не удалось сгенерировать айди для сущности order.");
                        }
                    }
                }
                return order;
            });
        } catch (SQLException e) {
            throw new SQLException("Ошибка при сохранении заказа", e);
        }
    }

    @Override
    public Order getOrderById(long id) throws SQLException {
        String sql = SqlQueries.SELECT_FROM.getSql().formatted("*", "orders", "id = ?");
        List<Order> orders = getOrders(sql, stmt -> stmt.setLong(1, id));
        return orders.isEmpty() ? null : orders.getFirst();
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted("*", "orders");
        return getOrders(sql, stmt -> {});
    }

    @Override
    public void updateOrder(Order order) throws SQLException {
        String sql = SqlQueries.UPDATE_SET.getSql().formatted("orders", "user_id = ?", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setLong(1, order.getUser().getId());
            stmt.setLong(2, order.getId());
        });
    }

    @Override
    public void deleteOrder(long id) throws SQLException {
        String sql = SqlQueries.DELETE_FROM.getSql().formatted("orders", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setLong(1, id);
        });
    }

    @Override
    public void addProductsToOrder(long orderId, List<Product> products) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection()) {
            DaoUtils.executeInTransaction(connection, () -> {
                addProductsToOrder(orderId, products, connection);
                return null;
            });
        }
    }

    @Override
    public List<Product> getProductsByOrderId(long orderId) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price " +
                "FROM products p " +
                "JOIN orders_products op ON p.id = op.product_id " +
                "WHERE op.order_id = ?";

        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setLong(1, orderId);
        }, rs -> {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            return products;
        });
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        return new Order.Builder()
                .withId(rs.getLong("id"))
                .withUser(new User.Builder()
                        .withId(rs.getLong("user_id"))
                        .build())
                .build();
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product.Builder()
                .withId(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withPrice(rs.getDouble("price"))
                .build();
    }

    private List<Order> getOrders(String sql, PreparedStatementSetter setter) throws SQLException {
        return DaoUtils.executeQuery(sql, setter, rs -> {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                long orderId = rs.getLong("id");
                List<Product> products = getProductsByOrderId(orderId); // Получаем связанные продукты
                orders.add(mapResultSetToOrder(rs).toBuilder().withProducts(products).build());
            }
            return orders;
        });
    }

    private void addProductsToOrder(long orderId, List<Product> products, Connection connection) throws SQLException {
        String insertOrderProductsSql = SqlQueries.INSERT_INTO.getSql().formatted("orders_products", "order_id, product_id", "?, ?");

        try (PreparedStatement insertOrderProductsStmt = connection.prepareStatement(insertOrderProductsSql)) {
            for (Product product : products) {
                insertOrderProductsStmt.setLong(1, orderId);
                insertOrderProductsStmt.setLong(2, product.getId());
                insertOrderProductsStmt.addBatch();
            }
            insertOrderProductsStmt.executeBatch();
        }
    }
}
