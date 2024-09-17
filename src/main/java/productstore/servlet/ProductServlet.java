package productstore.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import productstore.dao.impl.ProductDaoImpl;
import productstore.service.ProductService;
import productstore.service.apierror.ApiErrorResponse;
import productstore.service.apierror.ProductNotFoundException;
import productstore.service.impl.ProductServiceImpl;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.util.PaginationUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductDaoImpl());
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public void init() throws ServletException {
        System.out.println("ProductServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                int pageNumber = PaginationUtils.getPageNumber(req);
                int pageSize = PaginationUtils.getPageSize(req);

                List<ProductOutputDTO> products = productService.getProductsWithPagination(pageNumber, pageSize);
                writeResponse(resp, HttpServletResponse.SC_OK, products);
            } else if (pathInfo.matches("/\\d+/orders")) {
                long id = parseId(pathInfo.split("/")[1]);
                ProductOutputDTO product = productService.getProductWithOrdersById(id);
                writeResponse(resp, HttpServletResponse.SC_OK, product);
            } else if (pathInfo.matches("/\\d+")) {
                long id = parseId(pathInfo.substring(1));
                ProductOutputDTO product = productService.getProductById(id);
                writeResponse(resp, HttpServletResponse.SC_OK, product);
            } else {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL path");
            }
        } catch (ProductNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ProductInputDTO productInputDTO = gson.fromJson(requestBody, ProductInputDTO.class);

            ProductOutputDTO createdProduct = productService.createProduct(productInputDTO);
            writeResponse(resp, HttpServletResponse.SC_CREATED, createdProduct);
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ProductInputDTO productInputDTO = gson.fromJson(requestBody, ProductInputDTO.class);

            productService.updateProduct(productInputDTO);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (ProductNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID is required");
                return;
            }

            long id = parseId(pathInfo.substring(1));
            productService.deleteProduct(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (ProductNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid product ID format");
        }
    }

    private void handleException(HttpServletResponse resp, int statusCode, String message) throws IOException {
        writeResponse(resp, statusCode, new ApiErrorResponse(message, statusCode));
    }

    private void writeResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.setStatus(statusCode);
            String jsonResponse = gson.toJson(data);
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}