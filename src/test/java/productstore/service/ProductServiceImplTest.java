package productstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.ProductMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.repository.OrderRepository;
import productstore.repository.ProductRepository;
import productstore.service.exception.ProductNotFoundException;
import productstore.service.impl.ProductServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductInputDTO productInputDTO;
    private ProductOutputDTO productOutputDTO;
    private Order order;

    @BeforeEach
    public void setUp() {
        product = new Product("Test Product", 10.0);
        product.setId(1L);

        productInputDTO = new ProductInputDTO();
        productInputDTO.setName("Test Product");
        productInputDTO.setPrice(10.0);

        productOutputDTO = new ProductOutputDTO();
        productOutputDTO.setId(1L);
        productOutputDTO.setName("Test Product");
        productOutputDTO.setPrice(10.0);

        order = new Order();
        order.setId(1L);

        product.setOrders(new ArrayList<>(Collections.singletonList(order)));
        order.setOrderProducts(new ArrayList<>(Collections.singletonList(product)));
    }


    @Test
    @Transactional(readOnly = true)
    public void testFindById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testFindById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(productMapper.toDTOs(any())).thenReturn(Arrays.asList(productOutputDTO));

        List<ProductOutputDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productOutputDTO.getId(), result.get(0).getId());

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDTOs(any());
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetProductById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productMapper.toDTO(any())).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(productOutputDTO.getId(), result.getId());

        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).toDTO(product);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testSaveProduct() {
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDTO(any())).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.saveProduct(productInputDTO);

        assertNotNull(result);
        assertEquals(productOutputDTO.getId(), result.getId());

        verify(productMapper, times(1)).toEntity(productInputDTO);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toDTO(product);
    }

    @Test
    @Transactional
    public void testDeleteProductById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.deleteProductById(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).delete(product);
    }


    @Test
    @Transactional
    public void testDeleteProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testUpdateProductById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDTO(any())).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.updateProductById(1L, productInputDTO);

        assertNotNull(result);
        assertEquals(productOutputDTO.getId(), result.getId());
        assertEquals(productInputDTO.getName(), result.getName());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toDTO(product);
    }

    @Test
    @Transactional
    public void testUpdateProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProductById(1L, productInputDTO));

        verify(productRepository, times(1)).findById(1L);
    }
}
