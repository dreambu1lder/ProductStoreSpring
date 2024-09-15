package productstore.service;

import productstore.servlet.dto.input.UserInputDTO;
import productstore.servlet.dto.output.UserOutputDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserService {

    UserOutputDTO createUser(UserInputDTO userInputDTO) throws SQLException;

    UserOutputDTO getUserById(long id) throws SQLException;

    List<UserOutputDTO> getAllUsers() throws SQLException;

    List<UserOutputDTO> getUsersWithPagination(int pageNumber, int pageSize) throws SQLException;

    void updateUser(UserInputDTO userInputDTO) throws SQLException;

    void deleteUser(long id) throws SQLException;
}
