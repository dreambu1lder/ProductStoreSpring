package productstore.servlet.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.Product;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    
    @Mapping(target = "id", source = "id") 
    @Mapping(target = "orders", ignore = true) 
    Product toProduct(ProductInputDTO productInputDTO);

    @Mapping(target = "orderIds", expression = "java(includeOrderIds ? ordersToOrderIds(product.getOrders()) : null)")
    ProductOutputDTO toProductOutputDTO(@Context boolean includeOrderIds, Product product);

    List<ProductOutputDTO> toProductOutputDTOList(@Context boolean includeOrderIds, List<Product> products);

    default List<Long> ordersToOrderIds(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }
}