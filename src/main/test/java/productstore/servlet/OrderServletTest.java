package productstore.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.service.OrderService;
import productstore.service.apierror.OrderNotFoundException;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.dto.output.UserOutputDTO;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class OrderServletTest {

    @Mock
    private OrderService orderService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;
    private OrderServlet orderServlet;
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        
        orderServlet = new OrderServlet(orderService);
    }

    @Test
    public void testDoGet_allOrders() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/");

        List<OrderOutputDTO> orders = new ArrayList<>();
        orders.add(new OrderOutputDTO()); 
        when(orderService.getOrdersWithPagination(anyInt(), anyInt())).thenReturn(orders);

        orderServlet.doGet(request, response);

        
        verify(orderService, times(1)).getOrdersWithPagination(anyInt(), anyInt());
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("[")); 
    }

    @Test
    public void testDoGet_orderById() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1");

        OrderOutputDTO order = new OrderOutputDTO();
        when(orderService.getOrderById(1L)).thenReturn(order);

        orderServlet.doGet(request, response);

        
        verify(orderService, times(1)).getOrderById(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{")); 
    }

    @Test
    public void testDoGet_orderById_notFound() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/999");

        when(orderService.getOrderById(999L)).thenReturn(null); 

        orderServlet.doGet(request, response);

        
        verify(orderService, times(1)).getOrderById(999L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Order not found")); 
    }

    @Test
    public void testDoPost() throws Exception {
        
        String jsonRequest = gson.toJson(new OrderInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        OrderOutputDTO createdOrder = new OrderOutputDTO();
        when(orderService.createOrder(any(OrderInputDTO.class))).thenReturn(createdOrder);

        orderServlet.doPost(request, response);

        
        verify(orderService, times(1)).createOrder(any(OrderInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{")); 
    }

    @Test
    public void testDoPost_emptyBody() throws Exception {
        
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        orderServlet.doPost(request, response);

        
        String jsonResponse = responseWriter.toString();
        System.out.println("JSON Response: " + jsonResponse); 

        
        assertTrue(jsonResponse.contains("Request body is empty")); 
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_invalidJsonFormat() throws Exception {
        
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{invalidJson}")));

        orderServlet.doPost(request, response);

        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid JSON format")); 
    }

    @Test
    public void testDoPut() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new OrderInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        doNothing().when(orderService).updateOrder(any(OrderInputDTO.class));

        orderServlet.doPut(request, response);

        
        verify(orderService, times(1)).updateOrder(any(OrderInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoPut_orderNotFound() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new OrderInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        
        doThrow(new OrderNotFoundException("Order not found")).when(orderService).updateOrder(any(OrderInputDTO.class));

        orderServlet.doPut(request, response);

        
        verify(orderService, times(1)).updateOrder(any(OrderInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Order not found")); 
    }

    @Test
    public void testHandleGetUserByOrderId() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1/users");

        OrderOutputDTO order = new OrderOutputDTO();
        UserOutputDTO user = new UserOutputDTO();
        order.setUser(user);

        when(orderService.getOrderById(1L)).thenReturn(order);

        orderServlet.doGet(request, response);

        
        verify(orderService, times(1)).getOrderById(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{")); 
    }

    @Test
    public void testHandleGetProductsByOrderId() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1/products");

        List<ProductOutputDTO> products = Collections.singletonList(new ProductOutputDTO());

        when(orderService.getProductsByOrderId(1L)).thenReturn(products);

        orderServlet.doGet(request, response);

        
        verify(orderService, times(1)).getProductsByOrderId(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("[")); 
    }

    @Test
    public void testDoGet_invalidOrderIdFormat() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/invalidId");

        orderServlet.doGet(request, response);

        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid request path")); 
    }

    @Test
    public void testDoPut_invalidOrderIdFormat() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/invalidId");

        orderServlet.doPut(request, response);

        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid order ID format.")); 
    }

    @Test
    public void testDoPut_emptyBody() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        orderServlet.doPut(request, response);

        
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Request body is empty")); 
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_runtimeException() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new OrderInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        
        doThrow(new RuntimeException("Unexpected error")).when(orderService).updateOrder(any(OrderInputDTO.class));

        orderServlet.doPut(request, response);

        
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Unexpected error")); 
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoPut_emptyBody_addProductsToOrder() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/products");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        orderServlet.doPut(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Product IDs are required.")); 
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_order() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doNothing().when(orderService).deleteOrder(1L);

        orderServlet.doDelete(request, response);

        verify(orderService, times(1)).deleteOrder(1L);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_orderNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doThrow(new OrderNotFoundException("Order not found")).when(orderService).deleteOrder(1L);

        orderServlet.doDelete(request, response);

        verify(orderService, times(1)).deleteOrder(1L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Order not found")); 
    }

    
}

