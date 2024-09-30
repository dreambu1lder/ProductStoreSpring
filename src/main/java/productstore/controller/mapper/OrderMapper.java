package productstore.controller.mapper;

import org.mapstruct.*;
import productstore.model.Order;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.model.Product;
import productstore.model.User;

import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class, ProductMapper.class})
public interface OrderMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "products", source = "products")
    OrderOutputDTO toOrderOutputDTO(@Context boolean includeOrderIds, Order order);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "products", ignore = true) // Will be set separately
    Order toOrder(OrderInputDTO orderInputDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "products", source = "products")
    Order toOrder(OrderInputDTO orderInputDTO, User user, List<Product> products);

    List<OrderOutputDTO> toOrderOutputDTOList(@Context boolean includeOrderIds, List<Order> orders);
}
