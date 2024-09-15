package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    OrderOutputDTO toOrderOutputDTO(Order order);

    @Mapping(source = "userId", target = "user.id")
    Order toOrder(OrderInputDTO orderInputDTO);
}
