package productstore.dao.impl;

import productstore.dao.SqlQueries;
import productstore.dao.UserDao;
import productstore.dao.util.DaoUtils;
import productstore.model.Order;
import productstore.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {

    @Override
    public List<User> getUserWithPagination(int pageNumber, int pageSize) throws SQLException {
        String sql = SqlQueries.SELECT_USER_WITH_PAGINATION.getSql();
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (pageNumber - 1) * pageSize);
        }, this::mapResultSetToUsers);
    }

    @Override
    public User saveUser(User user) throws SQLException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new SQLException("Имя пользователя не может быть пустым.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new SQLException("Email пользователя не может быть пустым.");
        }
        String sql = SqlQueries.INSERT_USER.getSql();
        return DaoUtils.executeInsert(sql, stmt -> {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
        }, generatedKeys -> {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
                return user;
            } else {
                throw new SQLException("Не удалось сгенерировать айди для сущности user.");
            }
        });
    }

    @Override
    public User getUserById(long id) throws SQLException {
        String sql = SqlQueries.SELECT_USER_BY_ID.getSql();
        return DaoUtils.executeQuery(sql, stmt -> stmt.setLong(1, id), rs -> {
            Map<Long, User> userMap = mapResultSetToUsersWithOrders(rs);
            return userMap.isEmpty() ? null : userMap.values().iterator().next();
        });
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted("u.id AS user_id, u.name, u.email, o.id AS order_id", "users u LEFT JOIN orders o ON u.id = o.user_id");
        return DaoUtils.executeQuery(sql, stmt -> {}, this::mapResultSetToUsers);
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = SqlQueries.UPDATE_SET.getSql().formatted("users", "name = ?, email = ?", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setLong(3, user.getId());
        });
    }

    @Override
    public void deleteUser(long id) throws SQLException {

        String deleteOrdersSql = SqlQueries.DELETE_USER_ORDERS.getSql();
        DaoUtils.executeUpdate(deleteOrdersSql, stmt -> stmt.setLong(1, id));


        String deleteUserSql = SqlQueries.DELETE_USER.getSql();
        DaoUtils.executeUpdate(deleteUserSql, stmt -> stmt.setLong(1, id));
    }

    private Map<Long, User> mapResultSetToUsersWithOrders(ResultSet rs) throws SQLException {
        Map<Long, User> userMap = new HashMap<>();
        while (rs.next()) {
            long userId = rs.getLong("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = mapResultSetToUser(rs);
                userMap.put(userId, user);
            }

            long orderId = rs.getLong("order_id");
            if (orderId > 0) {
                user.getOrders().add(new Order.Builder().withId(orderId).build());
            }
        }
        return userMap;
    }

    private List<User> mapResultSetToUsers(ResultSet rs) throws SQLException {
        return new ArrayList<>(mapResultSetToUsersWithOrders(rs).values());
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .withId(rs.getLong("user_id"))
                .withName(rs.getString("name"))
                .withEmail(rs.getString("email"))
                .withOrders(new ArrayList<>())
                .build();
    }
}
