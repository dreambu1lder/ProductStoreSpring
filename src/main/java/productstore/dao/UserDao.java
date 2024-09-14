package productstore.dao;

import productstore.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    User saveUser(User user) throws SQLException;
    User getUserById(long id) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    List<User> getUserWithPagination(int pageNumber, int pageSize) throws SQLException;
    void updateUser(User user) throws SQLException;
    void deleteUser(long id) throws SQLException;
}
