package productstore.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import productstore.dao.impl.OrderDaoImpl;
import productstore.dao.impl.ProductDaoImpl;
import productstore.service.apierror.ApiErrorResponse;
import productstore.service.apierror.OrderNotFoundException;
import productstore.servlet.dto.OrderDTO;
import productstore.service.OrderService;
import productstore.service.impl.OrderServiceImpl;
import productstore.servlet.dto.ProductDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderServiceImpl(new OrderDaoImpl(), new ProductDaoImpl());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllOrders(resp);
            } else if (pathInfo.matches("/\\d+/products")) {
                handleGetProductsByOrderId(resp, pathInfo);
            } else if (pathInfo.matches("/\\d+")) {
                handleGetOrderById(resp, pathInfo);
            } else {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (OrderNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private void handleGetAllOrders(HttpServletResponse resp) throws IOException, SQLException {
        List<OrderDTO> orders = orderService.getAllOrders();
        writeResponse(resp, HttpServletResponse.SC_OK, orders);
    }

    private void handleGetProductsByOrderId(HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        long id = Long.parseLong(pathInfo.split("/")[1]);
        List<ProductDTO> products = orderService.getProductsByOrderId(id);
        writeResponse(resp, HttpServletResponse.SC_OK, products);
    }

    private void handleGetOrderById(HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        long id = Long.parseLong(pathInfo.split("/")[1]);
        OrderDTO order = orderService.getOrderById(id);
        writeResponse(resp, HttpServletResponse.SC_OK, order);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (req.getReader().ready()) {
                OrderDTO orderDTO = gson.fromJson(req.getReader(), OrderDTO.class);
                OrderDTO createdOrder = orderService.createOrder(orderDTO);
                writeResponse(resp, HttpServletResponse.SC_CREATED, createdOrder);
            } else {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
            }
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("/\\d+/products")) {
                if (req.getReader().ready()) {
                    long orderId = Long.parseLong(pathInfo.split("/")[1]);
                    // Используем TypeToken для корректной десериализации в List<Long>
                    List<Long> productIds = gson.fromJson(req.getReader(), new TypeToken<List<Long>>() {}.getType());
                    orderService.addProductsToOrder(orderId, productIds);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
                }
            } else {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
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
