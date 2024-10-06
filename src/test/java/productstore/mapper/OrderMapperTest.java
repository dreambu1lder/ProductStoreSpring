package productstore.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.mapper.OrderMapper;
import productstore.controller.mapper.OrderMapperImpl;
import productstore.controller.mapper.ProductMapper;
import productstore.controller.mapper.UserMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;
import productstore.service.ProductService;
import productstore.service.UserService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderMapperTest {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    // Используемые мапперы
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private OrderMapperImpl orderMapper;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        // Вручную устанавливаем зависимости в маппер
        orderMapper.setUserService(userService);
        orderMapper.setProductService(productService);

        // Устанавливаем productMapper и userMapper вручную, поскольку @InjectMocks не инициализирует их
        Field productMapperField = orderMapper.getClass().getDeclaredField("productMapper");
        productMapperField.setAccessible(true);
        productMapperField.set(orderMapper, productMapper);

        Field userMapperField = orderMapper.getClass().getDeclaredField("userMapper");
        userMapperField.setAccessible(true);
        userMapperField.set(orderMapper, userMapper);
    }

    @Test
    void shouldMapOrderToDTO() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderProducts(Arrays.asList(product));

        // When
        OrderOutputDTO dto = orderMapper.toDTO(order);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNotNull(dto.getUser());
        assertEquals(1L, dto.getUser().getId());
        assertEquals(1, dto.getProducts().size());
        assertEquals(1L, dto.getProducts().get(0).getId());
    }

    @Test
    void shouldMapOrderInputDTOToEntity() {
        // Given
        OrderInputDTO inputDTO = new OrderInputDTO();
        inputDTO.setUserId(1L);
        inputDTO.setProductIds(Arrays.asList(1L, 2L));

        User user = new User();
        when(userService.findById(1L)).thenReturn(user);

        Product product1 = new Product();
        Product product2 = new Product();
        when(productService.findById(1L)).thenReturn(product1);
        when(productService.findById(2L)).thenReturn(product2);

        // When
        Order order = orderMapper.toEntity(inputDTO);

        // Then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(2, order.getOrderProducts().size());
        assertTrue(order.getOrderProducts().contains(product1));
        assertTrue(order.getOrderProducts().contains(product2));
    }

    @Test
    void shouldMapMultipleOrdersToDTOs() {
        // Given
        User user1 = new User();
        user1.setId(1L);

        Product product1 = new Product();
        product1.setId(1L);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUser(user1);
        order1.setOrderProducts(Arrays.asList(product1));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(user1);
        order2.setOrderProducts(Arrays.asList(product1));

        // When
        List<OrderOutputDTO> dtos = orderMapper.toDTOs(Arrays.asList(order1, order2));

        // Then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(2L, dtos.get(1).getId());
    }
}
