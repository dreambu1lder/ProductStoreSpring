package productstore.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import productstore.dao.impl.OrderDaoImpl;
import productstore.dao.impl.ProductDaoImpl;
import productstore.service.apierror.ApiErrorResponse;
import productstore.service.apierror.OrderNotFoundException;
import productstore.service.OrderService;
import productstore.service.apierror.ProductNotFoundException;
import productstore.service.impl.OrderServiceImpl;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.input.ProductIdsRequest;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.dto.output.UserOutputDTO;
import productstore.servlet.mapper.OrderMapper;
import productstore.servlet.mapper.ProductMapper;
import productstore.servlet.util.PaginationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {

    private final OrderService orderService;
    private final Gson gson = new Gson();

    public OrderServlet() {
        this(new OrderServiceImpl(new OrderDaoImpl(), new ProductDaoImpl(), OrderMapper.INSTANCE, ProductMapper.INSTANCE));
    }

    public OrderServlet(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllOrders(resp, req);
            } else if (pathInfo.matches("/\\d+/products")) {
                handleGetProductsByOrderId(resp, pathInfo);
            } else if (pathInfo.matches("/\\d+/users")) {
                handleGetUserByOrderId(resp, pathInfo);
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

    private void handleGetUserByOrderId(HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        long orderId = Long.parseLong(pathInfo.split("/")[1]);

        OrderOutputDTO order = orderService.getOrderById(orderId);
        if (order == null) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, "Order with ID " + orderId + " not found.");
            return;
        }

        UserOutputDTO user = order.getUser();
        writeResponse(resp, HttpServletResponse.SC_OK, user);
    }

    private void handleGetAllOrders(HttpServletResponse resp, HttpServletRequest req) throws IOException, SQLException {
        try {
            int pageNumber = PaginationUtils.getPageNumber(req);
            int pageSize = PaginationUtils.getPageSize(req);

            List<OrderOutputDTO> orders = orderService.getOrdersWithPagination(pageNumber, pageSize);
            writeResponse(resp, HttpServletResponse.SC_OK, orders);
        } catch (NumberFormatException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid pagination parameters");
        }
    }

    private void handleGetProductsByOrderId(HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        long id = Long.parseLong(pathInfo.split("/")[1]);
        List<ProductOutputDTO> products = orderService.getProductsByOrderId(id);
        writeResponse(resp, HttpServletResponse.SC_OK, products);
    }

    private void handleGetOrderById(HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        long id = Long.parseLong(pathInfo.split("/")[1]);
        OrderOutputDTO order = orderService.getOrderById(id);
        if (order == null) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }
        writeResponse(resp, HttpServletResponse.SC_OK, order);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (requestBody.isEmpty()) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
                return;
            }

            OrderInputDTO orderInputDTO = gson.fromJson(requestBody, OrderInputDTO.class);
            OrderOutputDTO createdOrder = orderService.createOrder(orderInputDTO);
            writeResponse(resp, HttpServletResponse.SC_CREATED, createdOrder);

        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() < 2) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Order ID is required.");
            return;
        }

        if (pathInfo.matches("/\\d+/products")) {
            long orderId;
            try {
                orderId = Long.parseLong(pathInfo.split("/")[1]);
            } catch (NumberFormatException e) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
                return;
            }

            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ProductIdsRequest productIdsRequest;
            try {
                productIdsRequest = gson.fromJson(requestBody, ProductIdsRequest.class);
                if (productIdsRequest == null || productIdsRequest.getProductIds() == null || productIdsRequest.getProductIds().isEmpty()) {
                    handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Product IDs are required.");
                    return;
                }
            } catch (JsonSyntaxException e) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
                return;
            }

            try {
                orderService.addProductsToOrder(orderId, productIdsRequest.getProductIds());
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (OrderNotFoundException | ProductNotFoundException e) {
                handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (RuntimeException e) {
                handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
            } catch (SQLException e) {
                handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
            }
            return;
        }

        long orderId;
        try {
            orderId = Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
            return;
        }

        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        OrderInputDTO orderInputDTO;
        try {
            orderInputDTO = gson.fromJson(requestBody, OrderInputDTO.class);
            if (orderInputDTO == null) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
                return;
            }
            orderInputDTO.setId(orderId);
        } catch (JsonSyntaxException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
            return;
        }

        try {
            orderService.updateOrder(orderInputDTO);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (OrderNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        } catch (SQLException e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Order ID is required");
                return;
            }

            long id = parseId(pathInfo.substring(1));
            orderService.deleteOrder(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (OrderNotFoundException e) {
            handleException(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            handleException(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
        } catch (Exception e) {
            handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid order ID format");
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

