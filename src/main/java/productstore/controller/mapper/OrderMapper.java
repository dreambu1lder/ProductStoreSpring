package productstore.controller.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;
import productstore.service.ProductService;
import productstore.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {UserMapper.class, ProductMapper.class})
public abstract class OrderMapper {

    private UserService userService;
    private ProductService productService;

    // Сеттеры для внедрения зависимостей
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Mapping(target = "products", source = "orderProducts")
    @Mapping(target = "user", source = "user")
    public abstract OrderOutputDTO toDTO(Order order);

    @Mapping(target = "products", source = "orderProducts")
    @Mapping(target = "user", source = "user")
    public abstract List<OrderOutputDTO> toDTOs(List<Order> orders);


    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "orderProducts", source = "productIds", qualifiedByName = "mapProductIdsToProducts")
    @Mapping(target = "id", ignore = true)
    public abstract Order toEntity(OrderInputDTO orderInputDTO);

    @Named("mapUserIdToUser")
    public User mapUserIdToUser(Long userId) {
        return userService.findById(userId);
    }

    @Named("mapProductIdsToProducts")
    public List<Product> mapProductIdsToProducts(List<Long> productIds) {
        return productIds.stream()
                .map(productService::findById)
                .collect(Collectors.toList());
    }
}
