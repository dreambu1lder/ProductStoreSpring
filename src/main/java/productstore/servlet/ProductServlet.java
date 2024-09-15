package productstore.servlet;

import com.google.gson.Gson;
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
import productstore.servlet.dto.ProductDTO;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(new ProductDaoImpl());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Получение всех продуктов
                List<ProductDTO> products = productService.getAllProducts();
                writeResponse(resp, HttpServletResponse.SC_OK, products);
            } else if (pathInfo.matches("/\\d+/orders")) {
                // Новый маршрут для получения продуктов с заказами
                long id = parseId(pathInfo.split("/")[1]);
                ProductDTO product = productService.getProductWithOrdersById(id);
                writeResponse(resp, HttpServletResponse.SC_OK, product);
            } else if (pathInfo.matches("/\\d+")) {
                // Получение продукта по ID
                long id = parseId(pathInfo.substring(1));
                ProductDTO product = productService.getProductById(id);
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
            if (!req.getReader().ready()) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is required");
                return;
            }

            ProductDTO productDTO = gson.fromJson(req.getReader(), ProductDTO.class);
            ProductDTO createdProduct = productService.createProduct(productDTO);
            writeResponse(resp, HttpServletResponse.SC_CREATED, createdProduct);
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!req.getReader().ready()) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is required");
                return;
            }

            ProductDTO productDTO = gson.fromJson(req.getReader(), ProductDTO.class);
            productService.updateProduct(productDTO);
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
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
}
