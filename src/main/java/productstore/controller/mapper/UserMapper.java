package productstore.controller.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.model.Order;
import productstore.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "orderOutputDTOS", source = "orders", qualifiedByName = "orderToId")
    UserOutputDTO toDTO(User user);

    @Mapping(target = "orderOutputDTOS", source = "orders", qualifiedByName = "orderToId")
    List<UserOutputDTO> toDTOs(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(UserInputDTO userInputDTO);

    @Named("orderToId")
    default List<Long> mapOrdersToIds(List<Order> orders) {
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }
}