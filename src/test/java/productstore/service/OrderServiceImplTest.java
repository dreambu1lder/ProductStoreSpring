package productstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.dao.OrderDao;
import productstore.dao.ProductDao;
import productstore.model.Order;
import productstore.model.Product;
import productstore.service.apierror.OrderNotFoundException;
import productstore.service.apierror.ProductNotFoundException;
import productstore.service.apierror.ProductServiceException;
import productstore.service.impl.OrderServiceImpl;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.mapper.OrderMapper;
import productstore.servlet.mapper.ProductMapper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductMapper productMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        orderService = new OrderServiceImpl(orderDao, productDao, orderMapper, productMapper);
    }

    

    

    @Test
    public void testCreateOrderWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    }

    @Test
    public void testCreateOrderWithEmptyProductList() {
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO.setProductIds(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderInputDTO));
    }

    @Test
    public void testCreateOrderWithNonExistingProduct() throws SQLException {
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO.setProductIds(Arrays.asList(1L));

        when(productDao.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(orderInputDTO));
    }

    @Test
    public void testGetOrderByIdNotFound() throws SQLException {
        when(orderDao.getOrderById(1L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    public void testAddProductsToOrderWithNonExistingOrder() throws SQLException {
        when(orderDao.getOrderById(1L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.addProductsToOrder(1L, Arrays.asList(1L)));
    }

    @Test
    public void testAddProductsToOrderWithNonExistingProduct() throws SQLException {
        Order order = new Order();
        when(orderDao.getOrderById(1L)).thenReturn(order);
        when(productDao.getProductById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> orderService.addProductsToOrder(1L, Arrays.asList(1L)));
    }

    @Test
    public void testAddProductsToOrderWithEmptyProductList() throws SQLException {
        Order order = new Order();
        when(orderDao.getOrderById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class, () -> orderService.addProductsToOrder(1L, Collections.emptyList()));
    }

    @Test
    public void testGetProductsByOrderIdNotFound() throws SQLException {
        when(orderDao.getOrderById(1L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.getProductsByOrderId(1L));
    }

    
    @Test
    public void testCreateOrderWithSQLException() throws SQLException {
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO.setProductIds(Arrays.asList(1L));

        Product product = new Product();
        product.setId(1L);

        when(productDao.getProductById(1L)).thenThrow(new SQLException("Database error"));

        RuntimeException exception = assertThrows(ProductServiceException.class, () -> orderService.createOrder(orderInputDTO));
        assertTrue(exception.getMessage().contains("Failed to retrieve product with ID 1"));
    }

    @Test
    public void testAddProductsToOrderWithSQLException() throws SQLException {
        Order order = new Order();
        when(orderDao.getOrderById(1L)).thenReturn(order);

        when(productDao.getProductById(1L)).thenThrow(new SQLException("Database error"));

        RuntimeException exception = assertThrows(ProductServiceException.class, () -> orderService.addProductsToOrder(1L, List.of(1L)));
        assertTrue(exception.getMessage().contains("Failed to retrieve product with ID 1"));
    }

    @Test
    public void testCreateOrderSuccess() throws SQLException {
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO.setProductIds(Arrays.asList(1L));

        Product product = new Product();
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);

        when(productDao.getProductById(1L)).thenReturn(product);
        when(orderMapper.toOrder(orderInputDTO)).thenReturn(order);
        when(orderDao.saveOrder(order)).thenReturn(order);
        when(orderMapper.toOrderOutputDTO(false, order)).thenReturn(new OrderOutputDTO());

        OrderOutputDTO result = orderService.createOrder(orderInputDTO);

        assertNotNull(result);
        verify(orderDao).saveOrder(order);
        verify(orderMapper).toOrderOutputDTO(false, order);
    }

    @Test
    public void testGetOrderByIdSuccess() throws SQLException {
        Order order = new Order();
        order.setId(1L);

        when(orderDao.getOrderById(1L)).thenReturn(order);
        when(orderMapper.toOrderOutputDTO(false, order)).thenReturn(new OrderOutputDTO());

        OrderOutputDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        verify(orderDao).getOrderById(1L);
        verify(orderMapper).toOrderOutputDTO(false, order);
    }

    @Test
    public void testUpdateOrderSuccess() throws SQLException {
        OrderInputDTO orderInputDTO = new OrderInputDTO();
        orderInputDTO.setId(1L);
        orderInputDTO.setProductIds(Arrays.asList(1L));

        Order existingOrder = new Order();
        existingOrder.setId(1L);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);

        when(orderDao.getOrderById(1L)).thenReturn(existingOrder);
        when(orderMapper.toOrder(orderInputDTO)).thenReturn(updatedOrder);

        orderService.updateOrder(orderInputDTO);

        verify(orderDao).updateOrder(updatedOrder);
    }

    @Test
    public void testAddProductsToOrderSuccess() throws SQLException {
        Order order = new Order();
        order.setId(1L);

        Product product = new Product();
        product.setId(1L);

        when(orderDao.getOrderById(1L)).thenReturn(order);
        when(productDao.getProductById(1L)).thenReturn(product);

        orderService.addProductsToOrder(1L, Arrays.asList(1L));

        verify(orderDao).addProductsToOrder(1L, Arrays.asList(product));
    }

    @Test
    public void testGetAllOrdersSuccess() throws SQLException {
        List<Order> orders = Arrays.asList(new Order(), new Order());

        when(orderDao.getAllOrders()).thenReturn(orders);
        when(orderMapper.toOrderOutputDTO(false, orders.get(0))).thenReturn(new OrderOutputDTO());
        when(orderMapper.toOrderOutputDTO(false, orders.get(1))).thenReturn(new OrderOutputDTO());

        List<OrderOutputDTO> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderDao).getAllOrders();
    }

    @Test
    public void testDeleteOrderSuccess() throws SQLException {
        Order order = new Order();
        order.setId(1L);

        when(orderDao.getOrderById(1L)).thenReturn(order);

        orderService.deleteOrder(1L);

        verify(orderDao).deleteOrder(1L);
    }

    @Test
    public void testDeleteOrderNotFound() throws SQLException {
        when(orderDao.getOrderById(1L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));
    }

    @Test
    public void testGetProductsByOrderIdSuccess() throws SQLException {
        Order order = new Order();
        order.setId(1L);
        Product product1 = new Product();
        Product product2 = new Product();
        order.setProducts(Arrays.asList(product1, product2));

        when(orderDao.getOrderById(1L)).thenReturn(order);
        when(productMapper.toProductOutputDTO(false, product1)).thenReturn(new ProductOutputDTO());
        when(productMapper.toProductOutputDTO(false, product2)).thenReturn(new ProductOutputDTO());

        List<ProductOutputDTO> result = orderService.getProductsByOrderId(1L);

        assertEquals(2, result.size());
        verify(orderDao).getOrderById(1L);
    }

    @Test
    public void testDeleteOrderWithSQLException() throws SQLException {
        Order order = new Order();
        order.setId(1L);

        when(orderDao.getOrderById(1L)).thenReturn(order);
        doThrow(new SQLException("Database error")).when(orderDao).deleteOrder(1L);

        SQLException exception = assertThrows(SQLException.class, () -> orderService.deleteOrder(1L));
        assertTrue(exception.getMessage().contains("Database error"));
    }
}

