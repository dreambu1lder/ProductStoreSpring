package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.UserChangeEmailDTO;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.controller.mapper.UserMapper;
import productstore.model.User;
import productstore.repository.UserRepository;
import productstore.service.UserService;
import productstore.service.exception.UserNotFoundException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<UserOutputDTO> getAllUsers() {
        return userMapper.toDTOs(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserOutputDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));

    }

    @Transactional
    public UserOutputDTO saveUser(UserInputDTO userDTO) {
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(userDTO)));
    }

    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserOutputDTO updateUserById(Long id, UserChangeEmailDTO userChangeEmailDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        user.setEmail(userChangeEmailDTO.getEmail());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }
}
