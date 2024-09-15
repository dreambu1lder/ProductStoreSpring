package productstore.service.impl;

import productstore.dao.UserDao;
import productstore.model.User;
import productstore.service.UserService;
import productstore.service.apierror.UserNotFoundException;
import productstore.servlet.dto.UserDTO;
import productstore.servlet.mapper.UserMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDTO createUser(UserDTO userDto) throws SQLException {
        User user = userMapper.toUser(userDto);
        User savedUser = userDao.saveUser(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(long id) throws SQLException {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        return userMapper.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() throws SQLException {
        List<User> users = userDao.getAllUsers();
        return users.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<User> users = userDao.getUserWithPagination(pageNumber, pageSize);
        return users.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(UserDTO userDto) throws SQLException {
        User user = userMapper.toUser(userDto);
        if (userDao.getUserById(user.getId()) == null) {
            throw new UserNotFoundException("User with ID " + user.getId() + " not found.");
        }
        userDao.updateUser(user);
    }

    @Override
    public void deleteUser(long id) throws SQLException {
        if (userDao.getUserById(id) == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        userDao.deleteUser(id);
    }
}
