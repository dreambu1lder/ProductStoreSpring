package productstore.service;

import productstore.servlet.dto.UserDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDto) throws SQLException;
    UserDTO getUserById(long id) throws SQLException;
    List<UserDTO> getAllUsers() throws SQLException;
    List<UserDTO> getUsersWithPagination(int pageNumber, int pageSize) throws SQLException;
    void updateUser(UserDTO userDto) throws SQLException;
    void deleteUser(long id) throws SQLException;
}
