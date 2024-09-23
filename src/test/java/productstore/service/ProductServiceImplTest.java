package productstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.dao.ProductDao;
import productstore.model.Product;
import productstore.service.apierror.ProductNotFoundException;
import productstore.service.impl.ProductServiceImpl;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.mapper.ProductMapper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock
    private ProductMapper productMapper;

    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productDao, productMapper);
    }

    
    @Test
    public void testCreateProduct() throws SQLException {
        ProductInputDTO productInputDTO = new ProductInputDTO();
        productInputDTO.setName("Test Product");
        productInputDTO.setPrice(100.0);

        Product product = new Product();
        when(productMapper.toProduct(productInputDTO)).thenReturn(product);

        Product savedProduct = new Product();
        savedProduct.setId(1L); 
        when(productDao.saveProduct(product)).thenReturn(savedProduct);

        ProductOutputDTO productOutputDTO = new ProductOutputDTO();
        when(productMapper.toProductOutputDTO(true, savedProduct)).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.createProduct(productInputDTO);

        assertNotNull(result);
        verify(productDao, times(1)).saveProduct(product);
        verify(productMapper, times(1)).toProduct(productInputDTO);
        verify(productMapper, times(1)).toProductOutputDTO(true, savedProduct);
    }

    
    @Test
    public void testGetProductById() throws SQLException {
        Product product = new Product();
        product.setId(1L); 
        when(productDao.getProductById(1L)).thenReturn(product);

        ProductOutputDTO productOutputDTO = new ProductOutputDTO();
        when(productMapper.toProductOutputDTO(true, product)).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.getProductById(1L);

        assertNotNull(result);
        verify(productDao, times(1)).getProductById(1L);
        verify(productMapper, times(1)).toProductOutputDTO(true, product);
    }

    
    @Test
    public void testGetProductByIdNotFound() throws SQLException {
        when(productDao.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    
    @Test
    public void testGetAllProducts() throws SQLException {
        Product product = new Product();
        List<Product> products = Collections.singletonList(product);
        when(productDao.getAllProducts()).thenReturn(products);

        ProductOutputDTO productOutputDTO = new ProductOutputDTO();
        when(productMapper.toProductOutputDTO(true, product)).thenReturn(productOutputDTO);

        List<ProductOutputDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        verify(productDao, times(1)).getAllProducts();
        verify(productMapper, times(1)).toProductOutputDTO(true, product);
    }

    
    @Test
    public void testGetAllProductsEmpty() throws SQLException {
        when(productDao.getAllProducts()).thenReturn(Collections.emptyList());

        List<ProductOutputDTO> result = productService.getAllProducts();

        assertEquals(0, result.size());
        verify(productDao, times(1)).getAllProducts();
    }

    
    @Test
    public void testGetProductsWithPagination() throws SQLException {
        Product product = new Product();
        List<Product> products = Arrays.asList(product);
        when(productDao.getProductWithPagination(1, 10)).thenReturn(products);

        ProductOutputDTO productOutputDTO = new ProductOutputDTO();
        when(productMapper.toProductOutputDTO(true, product)).thenReturn(productOutputDTO);

        List<ProductOutputDTO> result = productService.getProductsWithPagination(1, 10);

        assertEquals(1, result.size());
        verify(productDao, times(1)).getProductWithPagination(1, 10);
        verify(productMapper, times(1)).toProductOutputDTO(true, product);
    }

    
    @Test
    public void testUpdateProduct() throws SQLException {
        ProductInputDTO productInputDTO = new ProductInputDTO();
        productInputDTO.setId(1L);  
        productInputDTO.setName("Updated Product");
        productInputDTO.setPrice(150.0);

        Product product = new Product();
        product.setId(1L);  
        when(productMapper.toProduct(productInputDTO)).thenReturn(product);
        when(productDao.getProductById(1L)).thenReturn(product); 

        productService.updateProduct(productInputDTO);

        verify(productDao, times(1)).updateProduct(product);
    }

    
    @Test
    public void testUpdateProductNotFound() throws SQLException {
        ProductInputDTO productInputDTO = new ProductInputDTO();
        productInputDTO.setId(1L);

        when(productMapper.toProduct(productInputDTO)).thenReturn(new Product());
        when(productDao.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productInputDTO));
    }

    
    @Test
    public void testDeleteProduct() throws SQLException {
        Product product = new Product();
        when(productDao.getProductById(1L)).thenReturn(product);

        productService.deleteProduct(1L);

        verify(productDao, times(1)).deleteProduct(1L);
    }

    
    @Test
    public void testDeleteProductNotFound() throws SQLException {
        when(productDao.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    
    @Test
    public void testGetProductWithOrdersById() throws SQLException {
        Product product = new Product();
        when(productDao.getProductWithOrdersById(1L)).thenReturn(product);

        ProductOutputDTO productOutputDTO = new ProductOutputDTO();
        when(productMapper.toProductOutputDTO(true, product)).thenReturn(productOutputDTO);

        ProductOutputDTO result = productService.getProductWithOrdersById(1L);

        assertNotNull(result);
        verify(productDao, times(1)).getProductWithOrdersById(1L);
        verify(productMapper, times(1)).toProductOutputDTO(true, product);
    }

    
    @Test
    public void testGetProductWithOrdersByIdNotFound() throws SQLException {
        when(productDao.getProductWithOrdersById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.getProductWithOrdersById(1L));
    }
}
