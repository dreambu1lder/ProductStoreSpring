package productstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.UserChangeEmailDTO;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.controller.mapper.UserMapper;
import productstore.model.User;
import productstore.repository.UserRepository;
import productstore.service.exception.UserNotFoundException;
import productstore.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserInputDTO userInputDTO;
    private UserOutputDTO userOutputDTO;
    private UserChangeEmailDTO userChangeEmailDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        userInputDTO = new UserInputDTO();
        userInputDTO.setEmail("test@example.com");

        userOutputDTO = new UserOutputDTO();
        userOutputDTO.setId(1L);
        userOutputDTO.setEmail("test@example.com");

        userChangeEmailDTO = new UserChangeEmailDTO();
        userChangeEmailDTO.setEmail("updated@example.com");
    }

    @Test
    @Transactional(readOnly = true)
    public void testFindById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testFindById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toDTOs(any())).thenReturn(Arrays.asList(userOutputDTO));

        List<UserOutputDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userOutputDTO.getId(), result.get(0).getId());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTOs(any());
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(any())).thenReturn(userOutputDTO);

        UserOutputDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userOutputDTO.getId(), result.getId());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testSaveUser() {
        when(userMapper.toEntity(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userOutputDTO);

        UserOutputDTO result = userService.saveUser(userInputDTO);

        assertNotNull(result);
        assertEquals(userOutputDTO.getId(), result.getId());

        verify(userMapper, times(1)).toEntity(userInputDTO);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    @Transactional
    public void testDeleteUserById() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @Transactional
    public void testUpdateUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            UserOutputDTO dto = new UserOutputDTO();
            dto.setId(savedUser.getId());
            dto.setEmail(savedUser.getEmail());
            return dto;
        });

        UserOutputDTO result = userService.updateUserById(1L, userChangeEmailDTO);

        assertNotNull(result);
        assertEquals(userChangeEmailDTO.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    @Transactional
    public void testUpdateUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(1L, userChangeEmailDTO));

        verify(userRepository, times(1)).findById(1L);
    }
}
