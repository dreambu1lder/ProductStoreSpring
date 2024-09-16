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
import java.util.stream.Collectors;

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

                            // Добавляем продукты к заказу и устанавливаем двустороннюю связь
                            for (Product product : order.getProducts()) {
                                insertOrderProductsStmt.setLong(1, generatedOrderId);
                                insertOrderProductsStmt.setLong(2, product.getId());
                                insertOrderProductsStmt.addBatch();

                                // Устанавливаем двустороннюю связь
                                if (product.getOrders() == null) {
                                    product.setOrders(new ArrayList<>());
                                }
                                product.getOrders().add(order);
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
        String sql = "SELECT o.id AS order_id, o.user_id, u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                "p.id AS product_id, p.name AS product_name, p.price AS product_price " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN orders_products op ON o.id = op.order_id " +
                "LEFT JOIN products p ON op.product_id = p.id " +
                "WHERE o.id = ?";
        List<Order> orders = getOrders(sql, stmt -> stmt.setLong(1, id));
        return orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT o.id AS order_id, o.user_id, u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                "p.id AS product_id, p.name AS product_name, p.price AS product_price " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN orders_products op ON o.id = op.order_id " +
                "LEFT JOIN products p ON op.product_id = p.id";
        return getOrders(sql, stmt -> {});
    }

    @Override
    public void updateOrder(Order order) throws SQLException {
        String sql = SqlQueries.UPDATE_SET.getSql().formatted("orders", "user_id = ?", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setLong(1, order.getUser().getId());
            stmt.setLong(2, order.getId());
        });

        // Обновляем продукты заказа и двусторонние связи
        deleteProductsFromOrder(order.getId()); // Удаляем существующие связи
        addProductsToOrder(order.getId(), order.getProducts()); // Добавляем новые связи
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
                .withId(rs.getLong("order_id")) // Используем псевдоним order_id из запроса
                .withUser(mapResultSetToUser(rs)) // Устанавливаем пользователя
                .withProducts(new ArrayList<>()) // Инициализируем пустой список продуктов
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
        // Проверяем, есть ли продукт в результатах запроса (может быть null, если заказ не имеет продуктов)
        long productId = rs.getLong("product_id");
        if (rs.wasNull()) {
            return null; // Если product_id равен null, значит у заказа нет связанных продуктов
        }
        return new Product.Builder()
                .withId(productId)
                .withName(rs.getString("product_name"))
                .withPrice(rs.getDouble("product_price"))
                .withOrders(new ArrayList<>()) // Инициализируем пустой список заказов для двусторонней связи
                .build();
    }

    private List<Order> getOrders(String sql, PreparedStatementSetter setter) throws SQLException {
        return DaoUtils.executeQuery(sql, setter, rs -> {
            Map<Long, Order.Builder> orderMap = new HashMap<>(); // Используем карту для уникальности заказов
            while (rs.next()) {
                long orderId = rs.getLong("order_id");
                Order.Builder orderBuilder = orderMap.get(orderId);

                if (orderBuilder == null) {
                    // Если заказа еще нет в карте, создаем новый заказ и добавляем в карту
                    orderBuilder = mapResultSetToOrder(rs).toBuilder();
                    orderMap.put(orderId, orderBuilder);
                }

                // Добавляем продукт к заказу и устанавливаем двустороннюю связь, если он присутствует
                Product product = mapResultSetToProduct(rs);
                if (product != null) {
                    orderBuilder.getProducts().add(product);
                    product.getOrders().add(orderBuilder.build()); // Устанавливаем двустороннюю связь
                }
            }

            // Создаем окончательные заказы из карт
            return orderMap.values().stream()
                    .map(Order.Builder::build)
                    .collect(Collectors.toList());
        });
    }

    private void addProductsToOrder(long orderId, List<Product> products, Connection connection) throws SQLException {
        String insertOrderProductsSql = SqlQueries.INSERT_INTO.getSql().formatted("orders_products", "order_id, product_id", "?, ?");

        try (PreparedStatement insertOrderProductsStmt = connection.prepareStatement(insertOrderProductsSql)) {
            for (Product product : products) {
                insertOrderProductsStmt.setLong(1, orderId);
                insertOrderProductsStmt.setLong(2, product.getId());
                insertOrderProductsStmt.addBatch();

                // Устанавливаем двустороннюю связь
                if (product.getOrders() == null) {
                    product.setOrders(new ArrayList<>());
                }
                product.getOrders().add(new Order.Builder().withId(orderId).build());
            }
            insertOrderProductsStmt.executeBatch();
        }
    }

    private void deleteProductsFromOrder(long orderId) throws SQLException {
        String deleteOrderProductsSql = SqlQueries.DELETE_FROM.getSql().formatted("orders_products", "order_id = ?");
        DaoUtils.executeUpdate(deleteOrderProductsSql, stmt -> {
            stmt.setLong(1, orderId);
        });
    }
}
