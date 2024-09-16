package productstore.servlet.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.dto.output.UserOutputDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = {UserMapper.class, ProductMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Маппинг Order -> OrderOutputDTO
    @Mapping(target = "user", source = "user")
    @Mapping(target = "products", source = "products")
    OrderOutputDTO toOrderOutputDTO(@Context boolean includeOrderIds, Order order);

    // Маппинг OrderInputDTO -> Order
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "id", ignore = true) // Игнорируем, если ID генерируется на уровне базы данных
    @Mapping(target = "products", ignore = true) // Игнорируем, так как продукты могут устанавливаться позже
    Order toOrder(OrderInputDTO orderInputDTO);
}