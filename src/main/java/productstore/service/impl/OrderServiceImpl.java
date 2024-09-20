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
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderDao orderDao, ProductDao productDao, OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<OrderOutputDTO> getOrdersWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<Order> orders = orderDao.getOrdersWithPagination(pageNumber, pageSize);
        return orders.stream()
                .map(order -> orderMapper.toOrderOutputDTO(false, order))
                .collect(Collectors.toList());
    }

    @Override
    public OrderOutputDTO createOrder(OrderInputDTO orderInputDTO) throws SQLException {
        if (orderInputDTO == null) {
            throw new IllegalArgumentException("OrderInputDTO cannot be null.");
        }

        Order order = orderMapper.toOrder(orderInputDTO);

        List<Product> products = orderInputDTO.getProductIds().stream()
                .map(this::getProductById).collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product.");
        }

        order.setProducts(products);
        products.forEach(product -> {
            if (product.getOrders() == null) {
                product.setOrders(new ArrayList<>());
            }
            product.getOrders().add(order);
        });

        Order savedOrder = orderDao.saveOrder(order);
        return orderMapper.toOrderOutputDTO(false, savedOrder);
    }

    @Override
    public OrderOutputDTO getOrderById(long id) throws SQLException {
        Order order = orderDao.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        return orderMapper.toOrderOutputDTO(false, order);
    }

    @Override
    public void updateOrder(OrderInputDTO orderInputDTO) throws SQLException {
        if (orderInputDTO.getId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null.");
        }

        Order existingOrder = orderDao.getOrderById(orderInputDTO.getId());
        if (existingOrder == null) {
            throw new OrderNotFoundException("Order with ID " + orderInputDTO.getId() + " not found.");
        }

        Order order = orderMapper.toOrder(orderInputDTO);
        order.setProducts(existingOrder.getProducts()); 

        orderDao.updateOrder(order);
    }

    @Override
    public List<OrderOutputDTO> getAllOrders() throws SQLException {
        List<Order> orders = orderDao.getAllOrders();
        return orders.stream()
                .map(order -> orderMapper.toOrderOutputDTO(false, order))
                .collect(Collectors.toList());
    }

    @Override
    public void addProductsToOrder(long orderId, List<Long> productIds) throws SQLException {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }

        List<Product> products = productIds.stream()
                .map(this::getProductById).collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new IllegalArgumentException("At least one product must be added to the order.");
        }

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

    @Override
    public void deleteOrder(long id) throws SQLException {
        Order order = orderDao.getOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        orderDao.deleteOrder(id);
    }

    private Product getProductById(long productId) {
        try {
            Product product = productDao.getProductById(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product with ID " + productId + " not found.");
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product by ID: " + productId, e);
        }
    }
}
