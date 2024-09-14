package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import productstore.servlet.dto.OrderDTO;
import productstore.model.Order;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    OrderDTO toOrderDTO(Order order);

    @Mapping(source = "userId", target = "user.id")
    Order toOrder(OrderDTO orderDTO);
}
