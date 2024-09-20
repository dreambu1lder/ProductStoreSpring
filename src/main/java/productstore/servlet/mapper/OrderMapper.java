package productstore.servlet.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;


@Mapper(uses = {UserMapper.class, ProductMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "products", source = "products")
    OrderOutputDTO toOrderOutputDTO(@Context boolean includeOrderIds, Order order);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "id", source = "id") 
    @Mapping(target = "products", ignore = true)
    Order toOrder(OrderInputDTO orderInputDTO);
}
