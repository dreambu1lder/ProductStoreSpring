package productstore.dao;

public enum SqlQueries {

    
    INSERT_INTO("INSERT INTO %s (%s) VALUES (%s)"),
    SELECT_FROM("SELECT %s FROM %s WHERE %s"),
    SELECT_ALL_FROM("SELECT %s FROM %s"),
    SELECT_WITH_PAGINATION("SELECT %s FROM %s ORDER BY %s LIMIT %s OFFSET %s"),
    DELETE_FROM("DELETE FROM %s WHERE %s"),
    UPDATE_SET("UPDATE %s SET %s WHERE %s"),

    
    INSERT_ORDER("INSERT INTO orders (user_id) VALUES (?)"),
    INSERT_ORDER_PRODUCTS("INSERT INTO orders_products (order_id, product_id) VALUES (?, ?)"),
    SELECT_ORDERS_WITH_PAGINATION("SELECT o.id AS order_id, o.user_id, u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
            "p.id AS product_id, p.name AS product_name, p.price AS product_price " +
            "FROM orders o " +
            "JOIN users u ON o.user_id = u.id " +
            "LEFT JOIN orders_products op ON o.id = op.order_id " +
            "LEFT JOIN products p ON op.product_id = p.id " +
            "ORDER BY o.id LIMIT ? OFFSET ?"),
    SELECT_ORDER_BY_ID("SELECT o.id AS order_id, o.user_id, u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
            "p.id AS product_id, p.name AS product_name, p.price AS product_price " +
            "FROM orders o " +
            "JOIN users u ON o.user_id = u.id " +
            "LEFT JOIN orders_products op ON o.id = op.order_id " +
            "LEFT JOIN products p ON op.product_id = p.id WHERE o.id = ?"),
    DELETE_ORDER_PRODUCTS("DELETE FROM orders_products WHERE order_id = ?"),
    DELETE_ORDER("DELETE FROM orders WHERE id = ?"),

    
    INSERT_PRODUCT("INSERT INTO products (name, price) VALUES (?, ?)"),
    SELECT_PRODUCTS_BY_ORDER_ID("SELECT p.id AS product_id, p.name AS product_name, p.price AS product_price " +
            "FROM products p " +
            "JOIN orders_products op ON p.id = op.product_id " +
            "WHERE op.order_id = ?"),
    SELECT_PRODUCT_WITH_PAGINATION("SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?"),
    SELECT_PRODUCT_BY_ID("SELECT * FROM products WHERE id = ?"),
    DELETE_PRODUCT("DELETE FROM products WHERE id = ?"),
    DELETE_PRODUCT_FROM_ORDER_PRODUCTS("DELETE FROM orders_products WHERE product_id = ?"),
    SELECT_ORDERS_BY_PRODUCT_ID("SELECT o.id AS order_id " +
            "FROM orders o " +
            "JOIN orders_products op ON o.id = op.order_id " +
            "WHERE op.product_id = ?"),

    
    INSERT_USER("INSERT INTO users (name, email) VALUES (?, ?)"),
    SELECT_USER_WITH_PAGINATION("SELECT u.id AS user_id, u.name, u.email, o.id AS order_id FROM users u " +
            "LEFT JOIN orders o ON u.id = o.user_id ORDER BY u.id LIMIT ? OFFSET ?"),
    SELECT_USER_BY_ID("SELECT u.id AS user_id, u.name, u.email, o.id AS order_id FROM users u " +
            "LEFT JOIN orders o ON u.id = o.user_id WHERE u.id = ?"),
    DELETE_USER_ORDERS("DELETE FROM orders WHERE user_id = ?"),
    DELETE_USER("DELETE FROM users WHERE id = ?");

    private final String sql;

    SqlQueries(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
