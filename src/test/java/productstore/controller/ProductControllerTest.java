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
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.service.ProductService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {ProductController.class})
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductInputDTO productInputDTO;
    private ProductOutputDTO productOutputDTO;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        productInputDTO = new ProductInputDTO();
        productInputDTO.setName("Test Product");
        productInputDTO.setPrice(100.0);

        productOutputDTO = new ProductOutputDTO();
        productOutputDTO.setId(1L);
        productOutputDTO.setName("Test Product");
        productOutputDTO.setPrice(100.0);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        List<ProductOutputDTO> productList = Collections.singletonList(productOutputDTO);

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(productList.size()))
                .andExpect(jsonPath("$[0].id").value(productOutputDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(productOutputDTO.getName()))
                .andExpect(jsonPath("$[0].price").value(productOutputDTO.getPrice()));
    }

    @Test
    public void testSaveProduct() throws Exception {
        // Mocking service layer
        when(productService.saveProduct(any(ProductInputDTO.class))).thenReturn(productOutputDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productInputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(productOutputDTO.getName()))
                .andExpect(jsonPath("$.price").value(productOutputDTO.getPrice()));
    }

    @Test
    public void testGetProductById() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productOutputDTO);

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(productOutputDTO.getName()))
                .andExpect(jsonPath("$.price").value(productOutputDTO.getPrice()));
    }

    @Test
    public void testDeleteProductById() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateProductById() throws Exception {
        when(productService.updateProductById(anyLong(), any(ProductInputDTO.class))).thenReturn(productOutputDTO);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productInputDTO)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(productOutputDTO.getName()))
                .andExpect(jsonPath("$.price").value(productOutputDTO.getPrice()));
    }
}
