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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order saveOrder(Order order) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection()) {
            return DaoUtils.executeInTransaction(connection, () -> {
                String insertOrderSql = SqlQueries.INSERT_ORDER.getSql();
                String insertOrderProductsSql = SqlQueries.INSERT_ORDER_PRODUCTS.getSql();

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
    public List<Order> getOrdersWithPagination(int pageNumber, int pageSize) throws SQLException {
        String sql = SqlQueries.SELECT_ORDERS_WITH_PAGINATION.getSql();

        return getOrders(sql, stmt -> {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (pageNumber - 1) * pageSize);
        });
    }

    @Override
    public Order getOrderById(long id) throws SQLException {
        String sql = SqlQueries.SELECT_ORDER_BY_ID.getSql();

        List<Order> orders = getOrders(sql, stmt -> stmt.setLong(1, id));
        if (orders.isEmpty()) {
            return null;
        }

        Order order = orders.get(0);
        return order;
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted(
                "o.id AS order_id, o.user_id, u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                        "p.id AS product_id, p.name AS product_name, p.price AS product_price",
                "orders o JOIN users u ON o.user_id = u.id LEFT JOIN orders_products op ON o.id = op.order_id LEFT JOIN products p ON op.product_id = p.id"
        );
        return getOrders(sql, stmt -> {});
    }

    @Override
    public void updateOrder(Order order) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection()) {
            DaoUtils.executeInTransaction(connection, () -> {
                String sql = SqlQueries.UPDATE_SET.getSql().formatted("orders", "user_id = ?", "id = ?");
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, order.getUser().getId());
                    stmt.setLong(2, order.getId());
                    stmt.executeUpdate();
                }

                deleteProductsFromOrder(order.getId(), connection);

                addProductsToOrder(order.getId(), order.getProducts(), connection);

                Order updatedOrder = getOrderById(order.getId());

                return null;
            });
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void deleteOrder(long id) throws SQLException {
        String sql = SqlQueries.DELETE_ORDER.getSql();
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
        String sql = SqlQueries.SELECT_PRODUCTS_BY_ORDER_ID.getSql();

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
                .withId(rs.getLong("order_id"))
                .withUser(mapResultSetToUser(rs))
                .withProducts(new ArrayList<>())
                .build();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .withId(rs.getLong("user_id"))
                .withName(rs.getString("user_name"))
                .withEmail(rs.getString("user_email"))
                .build();
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        long productId = rs.getLong("product_id");
        if (rs.wasNull()) {
            return null;
        }
        return new Product.Builder()
                .withId(productId)
                .withName(rs.getString("product_name"))
                .withPrice(rs.getDouble("product_price"))
                .withOrders(new ArrayList<>())
                .build();
    }

    private List<Order> getOrders(String sql, PreparedStatementSetter setter) throws SQLException {
        return DaoUtils.executeQuery(sql, setter, rs -> {
            Map<Long, Order> orderMap = new HashMap<>();
            while (rs.next()) {
                long orderId = rs.getLong("order_id");

                Order order = orderMap.computeIfAbsent(orderId, k -> {
                    try {
                        return mapResultSetToOrder(rs);
                    } catch (SQLException e) {
                        return null;
                    }
                });

                Product product = mapResultSetToProduct(rs);
                if (product != null) {
                    if (order != null && order.getProducts() != null) {
                        order.getProducts().add(product);
                    }
                }
            }

            List<Order> orders = new ArrayList<>(orderMap.values());

            return orders;
        });
    }

    private void addProductsToOrder(long orderId, List<Product> products, Connection connection) throws SQLException {
        String insertOrderProductsSql = SqlQueries.INSERT_ORDER_PRODUCTS.getSql();
        String checkProductExistsSql = "SELECT 1 FROM orders_products WHERE order_id = ? AND product_id = ?";

        try (PreparedStatement insertOrderProductsStmt = connection.prepareStatement(insertOrderProductsSql);
             PreparedStatement checkProductExistsStmt = connection.prepareStatement(checkProductExistsSql)) {

            for (Product product : products) {
                checkProductExistsStmt.setLong(1, orderId);
                checkProductExistsStmt.setLong(2, product.getId());
                try (ResultSet rs = checkProductExistsStmt.executeQuery()) {
                    if (rs.next()) {
                        continue;
                    }
                }

                insertOrderProductsStmt.setLong(1, orderId);
                insertOrderProductsStmt.setLong(2, product.getId());
                insertOrderProductsStmt.addBatch();
            }
            insertOrderProductsStmt.executeBatch();
        } catch (SQLException e) {
            throw e;
        }
    }

    private void deleteProductsFromOrder(long orderId, Connection connection) throws SQLException {
        String deleteOrderProductsSql = SqlQueries.DELETE_ORDER_PRODUCTS.getSql();
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteOrderProductsSql)) {
            deleteStmt.setLong(1, orderId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
