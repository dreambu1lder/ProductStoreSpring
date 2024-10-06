package productstore.service;

import productstore.controller.dto.input.UserChangeEmailDTO;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findById(Long id);

    List<UserOutputDTO> getAllUsers();

    UserOutputDTO getUserById(Long id);

    UserOutputDTO saveUser(UserInputDTO userDTO);

    void deleteUserById(Long id);

    UserOutputDTO updateUserById(Long id, UserChangeEmailDTO userChangeEmailDTO);
}
