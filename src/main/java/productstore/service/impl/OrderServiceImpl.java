package productstore.service.impl;

import productstore.dao.OrderDao;
import productstore.dao.ProductDao;
import productstore.model.Order;
import productstore.model.Product;
import productstore.service.OrderService;
import productstore.service.apierror.OrderNotFoundException;
import productstore.service.apierror.ProductNotFoundException;
import productstore.servlet.dto.input.OrderInputDTO;
import productstore.servlet.dto.output.OrderOutputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.mapper.OrderMapper;
import productstore.servlet.mapper.ProductMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public OrderServiceImpl(OrderDao orderDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    @Override
    public OrderOutputDTO createOrder(OrderInputDTO orderInputDTO) throws SQLException {
        if (orderInputDTO == null) {
            throw new IllegalArgumentException("OrderInputDTO cannot be null.");
        }

        Order order = orderMapper.toOrder(orderInputDTO);

        // Получаем продукты по ID и проверяем на null
        List<Product> products = orderInputDTO.getProductIds().stream()
                .map(productId -> {
                    try {
                        Product product = productDao.getProductById(productId);
                        if (product == null) {
                            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
                        }
                        return product;
                    } catch (SQLException e) {
                        throw new RuntimeException("Error fetching product by ID: " + productId, e);
                    }
                }).collect(Collectors.toList());

        // Проверяем, что список продуктов не пустой
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product.");
        }

        // Устанавливаем двустороннюю связь
        order.setProducts(products);
        products.forEach(product -> {
            if (product.getOrders() == null) {
                product.setOrders(new ArrayList<>());
            }
            product.getOrders().add(order);
        });

        Order savedOrder = orderDao.saveOrder(order);
        return orderMapper.toOrderOutputDTO(false, savedOrder); // Передаем false для исключения вложенных orderIds
    }

    @Override
    public OrderOutputDTO getOrderById(long id) throws SQLException {
        Order order = orderDao.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        return orderMapper.toOrderOutputDTO(false, order); // Передаем false для исключения вложенных orderIds
    }

    @Override
    public List<OrderOutputDTO> getAllOrders() throws SQLException {
        List<Order> orders = orderDao.getAllOrders();
        return orders.stream()
                .map(order -> orderMapper.toOrderOutputDTO(false, order)) // Передаем false для исключения вложенных orderIds
                .collect(Collectors.toList());
    }

    @Override
    public void addProductsToOrder(long orderId, List<Long> productIds) throws SQLException {
        // Проверяем, что заказ существует
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }

        List<Product> products = productIds.stream()
                .map(productId -> {
                    try {
                        Product product = productDao.getProductById(productId);
                        if (product == null) {
                            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
                        }
                        return product;
                    } catch (SQLException e) {
                        throw new RuntimeException("Error fetching product by ID: " + productId, e);
                    }
                }).collect(Collectors.toList());

        // Проверяем, что список продуктов не пустой
        if (products.isEmpty()) {
            throw new IllegalArgumentException("At least one product must be added to the order.");
        }

        // Устанавливаем двустороннюю связь
        products.forEach(product -> {
            if (product.getOrders() == null) {
                product.setOrders(new ArrayList<>());
            }
            product.getOrders().add(order);
        });

        orderDao.addProductsToOrder(orderId, products);
    }

    @Override
    public List<ProductOutputDTO> getProductsByOrderId(long orderId) throws SQLException {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }
        return order.getProducts().stream()
                .map(product -> productMapper.toProductOutputDTO(false, product))
                .collect(Collectors.toList());
    }
}
