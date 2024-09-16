package productstore.servlet.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.Product;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Маппинг ProductInputDTO -> Product
    @Mapping(target = "id", ignore = true) // Игнорируем, если ID генерируется на уровне базы данных
    @Mapping(target = "orders", ignore = true) // Игнорируем, так как заказы могут устанавливаться позже
    Product toProduct(ProductInputDTO productInputDTO);

    // Маппинг Product -> ProductOutputDTO
    @Mapping(target = "orderIds", expression = "java(includeOrderIds ? ordersToOrderIds(product.getOrders()) : null)")
    ProductOutputDTO toProductOutputDTO(@Context boolean includeOrderIds, Product product);

    // Маппинг списка продуктов
    List<ProductOutputDTO> toProductOutputDTOList(@Context boolean includeOrderIds, List<Product> products);

    // Преобразование списка Order -> списка orderIds
    default List<Long> ordersToOrderIds(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }
}