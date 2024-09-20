package productstore.servlet.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.model.User;
import productstore.servlet.dto.output.UserOutputDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orders", ignore = true)
    User toUser(UserInputDTO userInputDTO);

    @Mapping(target = "orderIds", expression = "java(includeOrderIds ? ordersToOrderIds(user.getOrders()) : null)")
    UserOutputDTO toUserOutputDTO(@Context boolean includeOrderIds, User user);

    @Named("ordersToOrderIds")
    default List<Long> ordersToOrderIds(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }
}
