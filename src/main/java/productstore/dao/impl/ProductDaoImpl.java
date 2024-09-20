package productstore.dao.impl;

import productstore.dao.SqlQueries;
import productstore.dao.ProductDao;
import productstore.dao.util.DaoUtils;
import productstore.db.DataBaseUtil;
import productstore.model.Order;
import productstore.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public Product saveProduct(Product product) throws SQLException {
        if (product.getPrice() < 0) {
            throw new SQLException("Цена продукта не может быть отрицательной.");
        }
        String sql = SqlQueries.INSERT_PRODUCT.getSql();
        return DaoUtils.executeInsert(sql, stmt -> {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));

                
                populateProductOrders(Collections.singletonList(product));
                return product;
            } else {
                throw new SQLException("Не удалось сгенерировать айди для сущности product.");
            }
        });
    }

    @Override
    public List<Product> getProductWithPagination(int pageNumber, int pageSize) throws SQLException {
        String sql = SqlQueries.SELECT_PRODUCT_WITH_PAGINATION.getSql();
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (pageNumber - 1) * pageSize);
        }, rs -> {
            List<Product> products = mapResultSetToProducts(rs);
            populateProductOrders(products);
            return products;
        });
    }

    @Override
    public void deleteProduct(long id) throws SQLException {
        String sql = SqlQueries.DELETE_PRODUCT.getSql();
        DaoUtils.executeUpdate(sql, stmt -> stmt.setLong(1, id));
    }

    @Override
    public void updateProduct(Product product) throws SQLException {
        String sql = SqlQueries.UPDATE_SET.getSql().formatted("products", "name = ?, price = ?", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setLong(3, product.getId());
        });

        updateOrdersForProduct(product);
    }

    @Override
    public Product getProductById(long id) throws SQLException {
        String sql = SqlQueries.SELECT_PRODUCT_BY_ID.getSql();
        return DaoUtils.executeQuery(sql, stmt -> stmt.setLong(1, id), rs -> {
            if (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                populateProductOrders(Collections.singletonList(product));
                return product;
            }
            return null;
        });
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted("*", "products");
        List<Product> products = DaoUtils.executeQuery(sql, stmt -> {}, this::mapResultSetToProducts);

        populateProductOrders(products);
        return products;
    }

    @Override
    public Product getProductWithOrdersById(long id) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, o.id AS order_id " +
                "FROM products p " +
                "LEFT JOIN orders_products op ON p.id = op.product_id " +
                "LEFT JOIN orders o ON op.order_id = o.id " +
                "WHERE p.id = ?";

        return DaoUtils.executeQuery(sql, stmt -> stmt.setLong(1, id), rs -> {
            Product.Builder productBuilder = null;
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                if (productBuilder == null) {
                    productBuilder = mapResultSetToProduct(rs).toBuilder();
                }

                long orderId = rs.getLong("order_id");
                if (orderId > 0) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                    order.getProducts().add(productBuilder.build());
                }
            }

            return productBuilder != null ? productBuilder.withOrders(orders).build() : null;
        });
    }

    private List<Product> mapResultSetToProducts(ResultSet rs) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Product product = mapResultSetToProduct(rs);
            products.add(product);
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product.Builder()
                .withId(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withPrice(rs.getDouble("price"))
                .withOrders(new ArrayList<>()) 
                .build();
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        return new Order.Builder()
                .withId(rs.getLong("order_id"))
                .withProducts(new ArrayList<>()) 
                .build();
    }

    
    private void populateProductOrders(List<Product> products) throws SQLException {
        for (Product product : products) {
            List<Order> orders = getOrdersByProductId(product.getId());
            product.setOrders(orders); 
            for (Order order : orders) {
                order.getProducts().add(product); 
            }
        }
    }

    private List<Order> getOrdersByProductId(long productId) throws SQLException {
        String sql = SqlQueries.SELECT_ORDERS_BY_PRODUCT_ID.getSql();
        return DaoUtils.executeQuery(sql, stmt -> stmt.setLong(1, productId), rs -> {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;
        });
    }

    private void updateOrdersForProduct(Product product) throws SQLException {
        String deleteSql = SqlQueries.DELETE_PRODUCT_FROM_ORDER_PRODUCTS.getSql();
        DaoUtils.executeUpdate(deleteSql, stmt -> stmt.setLong(1, product.getId()));

        if (product.getOrders() != null && !product.getOrders().isEmpty()) {
            String insertSql = SqlQueries.INSERT_INTO.getSql().formatted("orders_products", "order_id, product_id", "?, ?");
            try (Connection connection = DataBaseUtil.getConnection();
                 PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                for (Order order : product.getOrders()) {
                    insertStmt.setLong(1, order.getId());
                    insertStmt.setLong(2, product.getId());
                    insertStmt.addBatch();

                    order.getProducts().add(product);
                }
                insertStmt.executeBatch();
            }
        }
    }
}
