package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.servlet.dto.OrderDTO;
import productstore.servlet.dto.ProductDTO;
import productstore.model.Product;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "orders", target = "orderIds", qualifiedByName = "ordersToOrderIds")
    ProductDTO toProductDTO(Product product);

    @Mapping(source = "orderIds", target = "orders", qualifiedByName = "orderIdsToOrders")
    Product toProduct(ProductDTO productDTO);

    // Преобразуем List<Order> в List<Long>
    @Named("ordersToOrderIds")
    default List<Long> ordersToOrderIds(List<Order> orders) {
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }

    // Преобразуем List<Long> в List<Order>
    @Named("orderIdsToOrders")
    default List<Order> orderIdsToOrders(List<Long> orderIds) {
        // Преобразуем список идентификаторов в список объектов Order с установленным только id.
        // Вы можете подгрузить реальные объекты Order из базы данных, если потребуется.
        return orderIds.stream()
                .map(id -> {
                    Order order = new Order();
                    order.setId(id);
                    return order;
                })
                .collect(Collectors.toList());
    }
}
