package productstore.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.Product;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.mapper.ProductMapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    public void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    public void testToProduct() {
        
        ProductInputDTO productInputDTO = new ProductInputDTO();
        productInputDTO.setId(1L);
        productInputDTO.setName("Test Product");

        
        Product product = productMapper.toProduct(productInputDTO);

        
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());

        
        assertNull(product.getOrders());
    }

    @Test
    public void testToProductOutputDTO_withIncludeOrderIds() {
        
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        Order order1 = new Order();
        order1.setId(101L);
        Order order2 = new Order();
        order2.setId(102L);

        product.setOrders(List.of(order1, order2));

        
        ProductOutputDTO productOutputDTO = productMapper.toProductOutputDTO(true, product);

        
        assertEquals(1L, productOutputDTO.getId());
        assertEquals("Test Product", productOutputDTO.getName());

        
        assertEquals(List.of(101L, 102L), productOutputDTO.getOrderIds());
    }

    @Test
    public void testToProductOutputDTO_withoutIncludeOrderIds() {
        
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        
        ProductOutputDTO productOutputDTO = productMapper.toProductOutputDTO(false, product);

        
        assertEquals(1L, productOutputDTO.getId());
        assertEquals("Test Product", productOutputDTO.getName());

        
        assertNull(productOutputDTO.getOrderIds());
    }

    @Test
    public void testToProductOutputDTOList() {
        
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        List<Product> productList = List.of(product1, product2);

        
        List<ProductOutputDTO> productOutputDTOList = productMapper.toProductOutputDTOList(true, productList);

        
        assertEquals(2, productOutputDTOList.size());

        
        assertEquals(1L, productOutputDTOList.get(0).getId());
        assertEquals(2L, productOutputDTOList.get(1).getId());
    }

    @Test
    public void testOrdersToOrderIds() {
        
        Order order1 = new Order();
        order1.setId(101L);
        Order order2 = new Order();
        order2.setId(102L);

        List<Order> orders = List.of(order1, order2);

        
        List<Long> orderIds = productMapper.ordersToOrderIds(orders);

        
        assertEquals(List.of(101L, 102L), orderIds);
    }

    @Test
    public void testOrdersToOrderIds_withNullOrders() {
        
        List<Long> orderIds = productMapper.ordersToOrderIds(null);

        
        assertTrue(orderIds.isEmpty());
    }
}
