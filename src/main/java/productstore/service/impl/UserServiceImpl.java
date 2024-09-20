package productstore.service.impl;

import productstore.dao.UserDao;
import productstore.model.User;
import productstore.service.UserService;
import productstore.service.apierror.UserNotFoundException;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.servlet.dto.output.UserOutputDTO;
import productstore.servlet.mapper.UserMapper;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {

    private static final String USER_WITH_ID = "User with ID ";
    private static final String NOT_FOUND = " not found.";

    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserOutputDTO createUser(UserInputDTO userInputDTO) throws SQLException {
        User user = userMapper.toUser(userInputDTO);
        User savedUser = userDao.saveUser(user);
        return userMapper.toUserOutputDTO(true, savedUser);
    }

    @Override
    public UserOutputDTO getUserById(long id) throws SQLException {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException(USER_WITH_ID + id + NOT_FOUND);
        }
        return userMapper.toUserOutputDTO(true, user);
    }

    @Override
    public List<UserOutputDTO> getAllUsers() throws SQLException {
        List<User> users = userDao.getAllUsers();
        return users.stream()
                .map(user -> userMapper.toUserOutputDTO(true, user))
                .toList();
    }

    @Override
    public List<UserOutputDTO> getUsersWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<User> users = userDao.getUserWithPagination(pageNumber, pageSize);
        return users.stream()
                .map(user -> userMapper.toUserOutputDTO(true, user))
                .toList();
    }

    @Override
    public void updateUser(UserInputDTO userInputDTO) throws SQLException {
        User user = userMapper.toUser(userInputDTO);
        if (userDao.getUserById(user.getId()) == null) {
            throw new UserNotFoundException(USER_WITH_ID + user.getId() + NOT_FOUND);
        }
        userDao.updateUser(user);
    }

    @Override
    public void deleteUser(long id) throws SQLException {
        if (userDao.getUserById(id) == null) {
            throw new UserNotFoundException(USER_WITH_ID + id + NOT_FOUND);
        }
        userDao.deleteUser(id);
    }
}
