package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.model.User;
import productstore.servlet.dto.output.UserOutputDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserInputDTO userInputDTO);

    UserOutputDTO toUserOutputDTO(User user);

    @Named("ordersToOrderIds")
    default List<Long> ordersToOrderIds(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }
}
