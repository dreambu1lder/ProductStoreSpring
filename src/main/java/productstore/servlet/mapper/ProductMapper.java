package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.Product;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toProduct(ProductInputDTO productInputDTO);

    ProductOutputDTO toProductOutputDTO(Product product);

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
