package productstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.OrderMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.repository.OrderRepository;
import productstore.service.exception.OrderNotFoundException;
import productstore.service.impl.OrderServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderInputDTO orderInputDTO;
    private OrderOutputDTO orderOutputDTO;
    private ProductIdsDTO productIdsDTO;
    private Product product;

    @BeforeEach
    public void setUp() {
        order = new Order();
        order.setId(1L);
        Product product1 = new Product("Test Product", 10.0);
        product1.setId(1L);
        order.setOrderProducts(Arrays.asList(product1));

        orderInputDTO = new OrderInputDTO();
        orderInputDTO.setUserId(1L);
        orderInputDTO.setProductIds(Arrays.asList(1L));

        productIdsDTO = new ProductIdsDTO();
        productIdsDTO.setProductIds(Arrays.asList(1L, 2L));

        orderOutputDTO = new OrderOutputDTO();
        orderOutputDTO.setId(1L);
        orderOutputDTO.setProducts(Arrays.asList(new ProductOutputDTO(1L, "Test Product", 10.0)));

        product = new Product("Test Product", 10.0);
        product.setId(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(orderMapper.toDTOs(any())).thenReturn(Arrays.asList(orderOutputDTO));

        List<OrderOutputDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderOutputDTO.getId(), result.get(0).getId());

        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toDTOs(any());
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(any())).thenReturn(orderOutputDTO);

        OrderOutputDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(orderOutputDTO.getId(), result.getId());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toDTO(order);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testSaveOrder() {
        when(orderMapper.toEntity(any())).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.toDTO(any())).thenReturn(orderOutputDTO);

        OrderOutputDTO result = orderService.saveOrder(orderInputDTO);

        assertNotNull(result);
        assertEquals(orderOutputDTO.getId(), result.getId());

        verify(orderMapper, times(1)).toEntity(orderInputDTO);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDTO(order);
    }

    @Test
    @Transactional
    public void testDeleteOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        orderService.deleteOrderById(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @Transactional
    public void testDeleteOrderById_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrderById(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testUpdateOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.mapProductIdsToProducts(any())).thenReturn(Arrays.asList(product));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.toDTO(any())).thenReturn(orderOutputDTO);

        OrderOutputDTO result = orderService.updateOrderById(1L, productIdsDTO);

        assertNotNull(result);
        assertEquals(orderOutputDTO.getId(), result.getId());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).mapProductIdsToProducts(productIdsDTO.getProductIds());
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDTO(order);
    }

    @Test
    @Transactional
    public void testUpdateOrderById_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderById(1L, productIdsDTO));

        verify(orderRepository, times(1)).findById(1L);
    }
}
