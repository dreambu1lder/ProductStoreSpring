package productstore.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.ProductMapper;
import productstore.model.Product;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void shouldMapProductToDTO() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(99.99);

        ProductOutputDTO dto = productMapper.toDTO(product);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Product", dto.getName());
        assertEquals(99.99, dto.getPrice());
    }

    @Test
    void shouldMapProductsToDTOs() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(50.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(150.0);

        List<Product> products = Arrays.asList(product1, product2);

        List<ProductOutputDTO> dtos = productMapper.toDTOs(products);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Product 1", dtos.get(0).getName());
        assertEquals(50.0, dtos.get(0).getPrice());

        assertEquals(2L, dtos.get(1).getId());
        assertEquals("Product 2", dtos.get(1).getName());
        assertEquals(150.0, dtos.get(1).getPrice());
    }

    @Test
    void shouldMapProductInputDTOToEntity() {
        ProductInputDTO inputDTO = new ProductInputDTO();
        inputDTO.setName("New Product");
        inputDTO.setPrice(120.0);

        Product product = productMapper.toEntity(inputDTO);

        assertNotNull(product);
        assertNull(product.getId());
        assertEquals("New Product", product.getName());
        assertEquals(120.0, product.getPrice());
    }
}
