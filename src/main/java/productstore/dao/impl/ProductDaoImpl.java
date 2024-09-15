package productstore.dao.impl;

import productstore.dao.SqlQueries;
import productstore.dao.ProductDao;
import productstore.dao.util.DaoUtils;
import productstore.model.Order;
import productstore.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public Product saveProduct(Product product) throws SQLException {
        return DaoUtils.executeInsert(SqlQueries.INSERT_INTO.getSql().formatted("products", "name, price", "?, ?"), stmt -> {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));
                return product;
            } else {
                throw new SQLException("Не удалось сгенерировать айди для сущности product.");
            }
        });
    }

    @Override
    public List<Product> getProductWithPagination(int pageNumber, int pageSize) throws SQLException {
        String sql = SqlQueries.SELECT_WITH_PAGINATION.getSql().formatted("*", "products", "id", "?", "?");
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (pageNumber - 1) * pageSize);
        }, this::mapResultSetToProducts);
    }

    @Override
    public void deleteProduct(long id) throws SQLException {
        String sql = SqlQueries.DELETE_FROM.getSql().formatted("products", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setLong(1, id);
        });
    }

    @Override
    public void updateProduct(Product product) throws SQLException {
        String sql = SqlQueries.UPDATE_SET.getSql().formatted("products", "name = ?, price = ?", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setLong(3, product.getId());
        });
    }

    @Override
    public Product getProductById(long id) throws SQLException {
        String sql = SqlQueries.SELECT_FROM.getSql().formatted("*", "products", "id = ?");
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setLong(1, id);
        }, rs -> {
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            return null;
        });
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted("*", "products");
        System.out.println("Executing SQL: " + sql);
        List<Product> products = DaoUtils.executeQuery(sql, stmt -> {}, this::mapResultSetToProducts);
        System.out.println("Products from DB: " + products); // И этот вывод
        return products;
    }

    @Override
    public Product getProductWithOrdersById(long id) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, o.id AS order_id " +
                "FROM products p " +
                "LEFT JOIN order_products op ON p.id = op.product_id " +
                "LEFT JOIN orders o ON op.order_id = o.id " +
                "WHERE p.id = ?";

        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setLong(1, id);
        }, rs -> {
            Product.Builder productBuilder = null;
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                if (productBuilder == null) {
                    productBuilder = mapResultSetToProduct(rs).toBuilder();
                }

                long orderId = rs.getLong("order_id");
                if (orderId > 0) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }

            return productBuilder != null ? productBuilder.withOrders(orders).build() : null;
        });
    }

    private List<Product> mapResultSetToProducts(ResultSet rs) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Product product = mapResultSetToProduct(rs);
            System.out.println("Mapped product: " + product); // Добавьте этот вывод
            products.add(product);
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product.Builder()
                .withId(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withPrice(rs.getDouble("price"))
                .build();
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        return new Order.Builder()
                .withId(rs.getLong("order_id"))
                .build();
    }
}
