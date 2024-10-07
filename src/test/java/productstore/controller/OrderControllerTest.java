package productstore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.service.OrderService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {OrderController.class})
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private OrderInputDTO orderInputDTO;
    private OrderOutputDTO orderOutputDTO;
    private ProductIdsDTO productIdsDTO;

    @BeforeEach
    public void setUp() {
        // Настройка MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        // Пример данных для тестирования
        UserOutputDTO user = new UserOutputDTO(1L, "Test User", "user@example.com");

        ProductOutputDTO product1 = new ProductOutputDTO(100L, "Product1", 10.0);
        ProductOutputDTO product2 = new ProductOutputDTO(101L, "Product2", 15.0);

        orderInputDTO = new OrderInputDTO();
        orderInputDTO.setUserId(1L);
        orderInputDTO.setProductIds(Arrays.asList(100L, 101L));

        orderOutputDTO = new OrderOutputDTO(1L, user, Arrays.asList(product1, product2));

        productIdsDTO = new ProductIdsDTO();
        productIdsDTO.setProductIds(Arrays.asList(200L, 201L));
    }

    @Test
    public void testSaveOrder() throws Exception {
        // Мокаем поведение сервиса
        when(orderService.saveOrder(any(OrderInputDTO.class))).thenReturn(orderOutputDTO);

        // Отправляем POST-запрос и проверяем результат
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderInputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderOutputDTO.getId()))
                .andExpect(jsonPath("$.user.id").value(orderOutputDTO.getUser().getId()))
                .andExpect(jsonPath("$.products.length()").value(Optional.of(orderOutputDTO.getProducts().size()).orElse(0)));
    }

    @Test
    public void testGetAllOrders() throws Exception {
        List<OrderOutputDTO> orderOutputDTOList = Arrays.asList(orderOutputDTO);

        // Мокаем поведение сервиса
        when(orderService.getAllOrders()).thenReturn(orderOutputDTOList);

        // Отправляем GET-запрос и проверяем результат
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(Optional.of(orderOutputDTOList.size()).orElse(0)))
                .andExpect(jsonPath("$[0].id").value(orderOutputDTO.getId()));
    }

    @Test
    public void testDeleteOrderById() throws Exception {
        // Мокаем поведение сервиса для удаления
        mockMvc.perform(delete("/api/orders/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetOrderById() throws Exception {
        // Мокаем поведение сервиса
        when(orderService.getOrderById(anyLong())).thenReturn(orderOutputDTO);

        // Отправляем GET-запрос и проверяем результат
        mockMvc.perform(get("/api/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderOutputDTO.getId()))
                .andExpect(jsonPath("$.user.id").value(orderOutputDTO.getUser().getId()));
    }

    @Test
    public void testUpdateOrderById() throws Exception {
        // Мокаем поведение сервиса
        when(orderService.updateOrderById(anyLong(), any(ProductIdsDTO.class))).thenReturn(orderOutputDTO);

        // Отправляем PUT-запрос и проверяем результат
        mockMvc.perform(put("/api/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productIdsDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderOutputDTO.getId()))
                .andExpect(jsonPath("$.user.id").value(orderOutputDTO.getUser().getId()));
    }
}
