package productstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.dao.UserDao;
import productstore.model.User;
import productstore.service.apierror.UserNotFoundException;
import productstore.service.impl.UserServiceImpl;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.servlet.dto.output.UserOutputDTO;
import productstore.servlet.mapper.UserMapper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userDao, userMapper);
    }

    
    @Test
    public void testCreateUser() throws SQLException {
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setName("Test User");
        userInputDTO.setEmail("test@example.com");

        User user = new User();
        when(userMapper.toUser(userInputDTO)).thenReturn(user);

        User savedUser = new User();
        savedUser.setId(1L); 
        when(userDao.saveUser(user)).thenReturn(savedUser);

        UserOutputDTO userOutputDTO = new UserOutputDTO();
        when(userMapper.toUserOutputDTO(true, savedUser)).thenReturn(userOutputDTO);

        UserOutputDTO result = userService.createUser(userInputDTO);

        assertNotNull(result);
        verify(userDao, times(1)).saveUser(user);
        verify(userMapper, times(1)).toUser(userInputDTO);
        verify(userMapper, times(1)).toUserOutputDTO(true, savedUser);
    }

    
    @Test
    public void testGetUserById() throws SQLException {
        User user = new User();
        user.setId(1L); 
        when(userDao.getUserById(1L)).thenReturn(user);

        UserOutputDTO userOutputDTO = new UserOutputDTO();
        when(userMapper.toUserOutputDTO(true, user)).thenReturn(userOutputDTO);

        UserOutputDTO result = userService.getUserById(1L);

        assertNotNull(result);
        verify(userDao, times(1)).getUserById(1L);
        verify(userMapper, times(1)).toUserOutputDTO(true, user);
    }

    
    @Test
    public void testGetUserByIdNotFound() throws SQLException {
        when(userDao.getUserById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    
    @Test
    public void testGetAllUsers() throws SQLException {
        User user = new User();
        List<User> users = Collections.singletonList(user);
        when(userDao.getAllUsers()).thenReturn(users);

        UserOutputDTO userOutputDTO = new UserOutputDTO();
        when(userMapper.toUserOutputDTO(true, user)).thenReturn(userOutputDTO);

        List<UserOutputDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userDao, times(1)).getAllUsers();
        verify(userMapper, times(1)).toUserOutputDTO(true, user);
    }

    
    @Test
    public void testGetAllUsersEmpty() throws SQLException {
        when(userDao.getAllUsers()).thenReturn(Collections.emptyList());

        List<UserOutputDTO> result = userService.getAllUsers();

        assertEquals(0, result.size());
        verify(userDao, times(1)).getAllUsers();
    }

    
    @Test
    public void testGetUsersWithPagination() throws SQLException {
        User user = new User();
        List<User> users = Arrays.asList(user);
        when(userDao.getUserWithPagination(1, 10)).thenReturn(users);

        UserOutputDTO userOutputDTO = new UserOutputDTO();
        when(userMapper.toUserOutputDTO(true, user)).thenReturn(userOutputDTO);

        List<UserOutputDTO> result = userService.getUsersWithPagination(1, 10);

        assertEquals(1, result.size());
        verify(userDao, times(1)).getUserWithPagination(1, 10);
        verify(userMapper, times(1)).toUserOutputDTO(true, user);
    }

    
    @Test
    public void testUpdateUser() throws SQLException {
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setId(1L);  
        userInputDTO.setName("Updated User");
        userInputDTO.setEmail("updated@example.com");

        User user = new User();
        user.setId(1L);  
        when(userMapper.toUser(userInputDTO)).thenReturn(user);
        when(userDao.getUserById(1L)).thenReturn(user); 

        userService.updateUser(userInputDTO);

        verify(userDao, times(1)).updateUser(user);
    }

    
    @Test
    public void testUpdateUserNotFound() throws SQLException {
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setId(1L);

        when(userMapper.toUser(userInputDTO)).thenReturn(new User());
        when(userDao.getUserById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userInputDTO));
    }

    
    @Test
    public void testDeleteUser() throws SQLException {
        User user = new User();
        when(userDao.getUserById(1L)).thenReturn(user);

        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteUser(1L);
    }

    
    @Test
    public void testDeleteUserNotFound() throws SQLException {
        when(userDao.getUserById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}


