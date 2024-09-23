package productstore.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.service.ProductService;
import productstore.service.apierror.ProductNotFoundException;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProductServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;
    private ProductServlet productServlet;
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        
        productServlet = new ProductServlet(productService);
    }

    
    @Test
    public void testDoGet_allProducts() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        List<ProductOutputDTO> products = new ArrayList<>();
        products.add(new ProductOutputDTO()); 
        when(productService.getProductsWithPagination(anyInt(), anyInt())).thenReturn(products);

        productServlet.doGet(request, response);

        verify(productService, times(1)).getProductsWithPagination(anyInt(), anyInt());
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("[")); 
    }

    
    @Test
    public void testDoGet_productById() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        ProductOutputDTO product = new ProductOutputDTO();
        when(productService.getProductById(1L)).thenReturn(product);

        productServlet.doGet(request, response);

        verify(productService, times(1)).getProductById(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{")); 
    }

    
    @Test
    public void testDoGet_productNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");

        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException("Product not found"));

        productServlet.doGet(request, response);

        verify(productService, times(1)).getProductById(999L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Product not found")); 
    }

    
    @Test
    public void testDoPost_createProduct() throws Exception {
        
        ProductInputDTO productInputDTO = new ProductInputDTO();
        productInputDTO.setName("Sample Product");
        productInputDTO.setPrice(100.0);

        
        String jsonRequest = gson.toJson(productInputDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        ProductOutputDTO createdProduct = new ProductOutputDTO();
        when(productService.createProduct(any(ProductInputDTO.class))).thenReturn(createdProduct);

        productServlet.doPost(request, response);

        verify(productService, times(1)).createProduct(any(ProductInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{")); 
    }
    
    @Test
    public void testDoPost_emptyBody() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        productServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Request body is empty"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    
    @Test
    public void testDoPost_invalidJsonFormat() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{invalidJson}")));

        productServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid JSON format"));
    }

    
    @Test
    public void testDoPut_updateProduct() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new ProductInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        doNothing().when(productService).updateProduct(any(ProductInputDTO.class));

        productServlet.doPut(request, response);

        verify(productService, times(1)).updateProduct(any(ProductInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    
    @Test
    public void testDoPut_productNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new ProductInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        doThrow(new ProductNotFoundException("Product not found")).when(productService).updateProduct(any(ProductInputDTO.class));

        productServlet.doPut(request, response);

        verify(productService, times(1)).updateProduct(any(ProductInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Product not found"));
    }

    
    @Test
    public void testDoPut_emptyBody() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        productServlet.doPut(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Request body is empty"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    
    @Test
    public void testDoDelete_deleteProduct() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doNothing().when(productService).deleteProduct(1L);

        productServlet.doDelete(request, response);

        verify(productService, times(1)).deleteProduct(1L);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    
    @Test
    public void testDoDelete_productNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");

        doThrow(new ProductNotFoundException("Product not found")).when(productService).deleteProduct(999L);

        productServlet.doDelete(request, response);

        verify(productService, times(1)).deleteProduct(999L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Product not found"));
    }

    @Test
    public void testDoGet_invalidPath() throws Exception {
        
        when(request.getPathInfo()).thenReturn("/invalidpath");

        productServlet.doGet(request, response);

        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid URL path"));
    }

    @Test
    public void testDoPost_missingFields() throws Exception {
        String jsonRequest = "{\"description\": \"A sample product without name and price\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        productServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Missing required fields"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_invalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalidId");

        productServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid product ID format"));
    }
}
