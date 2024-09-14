package productstore.dao.impl;

import productstore.dao.SqlQueries;
import productstore.dao.UserDao;
import productstore.dao.util.DaoUtils;
import productstore.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public List<User> getUserWithPagination(int pageNumber, int pageSize) throws SQLException {
        String sql = SqlQueries.SELECT_WITH_PAGINATION.getSql().formatted("*", "users", "id", "?", "?");
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (pageNumber - 1) * pageSize);
        }, this::mapResultSetToUsers);
    }

    @Override
    public User saveUser(User user) throws SQLException {
        return DaoUtils.executeInsert(SqlQueries.INSERT_INTO.getSql().formatted("users", "name, email", "?, ?"), stmt -> {
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
        String sql = SqlQueries.SELECT_FROM.getSql().formatted("*", "users", "id = ?");
        return DaoUtils.executeQuery(sql, stmt -> {
            stmt.setLong(1, id);
        }, rs -> {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        });
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = SqlQueries.SELECT_ALL_FROM.getSql().formatted("*", "users");
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
        String sql = SqlQueries.DELETE_FROM.getSql().formatted("users", "id = ?");
        DaoUtils.executeUpdate(sql, stmt -> {
            stmt.setLong(1, id);
        });
    }

    private List<User> mapResultSetToUsers(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(mapResultSetToUser(rs));
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .withId(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withEmail(rs.getString("email"))
                .build();
    }

}
