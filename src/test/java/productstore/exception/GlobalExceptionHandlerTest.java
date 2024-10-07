package productstore.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import productstore.controller.OrderController;
import productstore.controller.ProductController;
import productstore.controller.UserController;
import productstore.service.OrderService;
import productstore.service.ProductService;
import productstore.service.UserService;
import productstore.service.exception.GlobalExceptionHandler;
import productstore.service.exception.OrderNotFoundException;
import productstore.service.exception.ProductNotFoundException;
import productstore.service.exception.UserNotFoundException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserController(userService),
                        new OrderController(orderService),
                        new ProductController(productService))
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    public void testHandleUserNotFoundException() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new UserNotFoundException("User with id 999 not found."));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id 999 not found."));
    }

    @Test
    public void testHandleOrderNotFoundException() throws Exception {
        when(orderService.getOrderById(anyLong())).thenThrow(new OrderNotFoundException("Order with id 999 not found."));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order with id 999 not found."));
    }

    @Test
    public void testHandleProductNotFoundException() throws Exception {
        when(productService.getProductById(anyLong())).thenThrow(new ProductNotFoundException("Product with id 999 not found."));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product with id 999 not found."));
    }

    @Test
    public void testHandleValidationExceptions() throws Exception {
        String invalidRequestBody = "{ \"userId\": null, \"productIds\": [] }";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{"
                        + "\"userId\":\"User ID cannot be null\","
                        + "\"productIds\":\"Product IDs cannot be empty\""
                        + "}"));
    }
}
