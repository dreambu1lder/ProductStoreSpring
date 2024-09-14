package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import productstore.servlet.dto.UserDTO;
import productstore.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);
}
