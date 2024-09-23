package productstore.dao;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import productstore.dao.impl.OrderDaoImpl;
import productstore.dao.impl.ProductDaoImpl;
import productstore.dao.impl.UserDaoImpl;
import productstore.dao.utils.PostgreSQLContainerProvider;
import productstore.db.DataBaseUtil;
import productstore.model.Product;
import productstore.model.User;
import productstore.model.Order;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class OrderDaoImplTest {
    private OrderDaoImpl orderDao;

    @BeforeAll
    public static void setUpDatabase() {
        
        PostgreSQLContainerProvider.startContainer();
    }

    @BeforeEach
    public void setUp() {
        orderDao = new OrderDaoImpl();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders_products CASCADE;");
            stmt.execute("TRUNCATE TABLE orders CASCADE;");
            stmt.execute("TRUNCATE TABLE users CASCADE;");
            stmt.execute("TRUNCATE TABLE products CASCADE;");
        }
    }

    @AfterAll
    public static void tearDownAll() {
        
        PostgreSQLContainerProvider.stopContainer();
    }

    @Test
    public void testSaveOrder() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);
        Product product2 = createProduct("Product 2", 20.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1, product2))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        assertNotNull(savedOrder.getId());
        assertEquals(2, savedOrder.getProducts().size());
    }

    @Test
    public void testGetOrderById() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product = createProduct("Product 1", 10.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product))
                .build();

        Order savedOrder = orderDao.saveOrder(order);
        Order fetchedOrder = orderDao.getOrderById(savedOrder.getId());

        assertNotNull(fetchedOrder);
        assertEquals(savedOrder.getId(), fetchedOrder.getId());
        assertEquals(1, fetchedOrder.getProducts().size());
    }

    @Test
    public void testGetOrdersWithPagination() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product = createProduct("Product 1", 10.00);

        for (int i = 0; i < 10; i++) {
            Order order = new Order.Builder()
                    .withUser(user)
                    .withProducts(List.of(product))
                    .build();
            orderDao.saveOrder(order);
        }

        List<Order> firstPage = orderDao.getOrdersWithPagination(1, 5);
        List<Order> secondPage = orderDao.getOrdersWithPagination(2, 5);

        assertEquals(5, firstPage.size());
        assertEquals(5, secondPage.size());
    }

    @Test
    public void testUpdateOrder() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);
        Product product2 = createProduct("Product 2", 20.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(new ArrayList<>(List.of(product1)))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        
        List<Product> updatedProducts = new ArrayList<>(savedOrder.getProducts());
        updatedProducts.add(product2);
        savedOrder.setProducts(updatedProducts);
        orderDao.updateOrder(savedOrder);

        Order updatedOrder = orderDao.getOrderById(savedOrder.getId());
        assertEquals(2, updatedOrder.getProducts().size());
    }

    @Test
    public void testDeleteOrder() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product = createProduct("Product 1", 10.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product))
                .build();

        Order savedOrder = orderDao.saveOrder(order);
        orderDao.deleteOrder(savedOrder.getId());

        Order deletedOrder = orderDao.getOrderById(savedOrder.getId());
        assertNull(deletedOrder);
    }

    @Test
    public void testGetAllOrders() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product = createProduct("Product 1", 10.00);

        for (int i = 0; i < 3; i++) {
            Order order = new Order.Builder()
                    .withUser(user)
                    .withProducts(List.of(product))
                    .build();
            orderDao.saveOrder(order);
        }

        List<Order> orders = orderDao.getAllOrders();
        assertEquals(3, orders.size());
    }

    @Test
    public void testAddProductsToOrder() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);
        Product product2 = createProduct("Product 2", 20.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        
        orderDao.addProductsToOrder(savedOrder.getId(), List.of(product2));

        Order updatedOrder = orderDao.getOrderById(savedOrder.getId());
        assertEquals(2, updatedOrder.getProducts().size());
    }

    @Test
    public void testGetProductsByOrderId() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);
        Product product2 = createProduct("Product 2", 20.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1, product2))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        List<Product> products = orderDao.getProductsByOrderId(savedOrder.getId());
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getId() == product1.getId()));
        assertTrue(products.stream().anyMatch(p -> p.getId() == product2.getId()));
    }

    @Test
    public void testDeleteOrderWithProducts() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);
        Product product2 = createProduct("Product 2", 20.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1, product2))
                .build();

        Order savedOrder = orderDao.saveOrder(order);
        long orderId = savedOrder.getId();

        orderDao.deleteOrder(orderId);

        Order deletedOrder = orderDao.getOrderById(orderId);
        assertNull(deletedOrder);

        
        List<Product> products = orderDao.getProductsByOrderId(orderId);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testSaveOrderWithoutProducts() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(new ArrayList<>()) 
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        assertNotNull(savedOrder.getId());
        assertTrue(savedOrder.getProducts().isEmpty());
    }

    @Test
    public void testUpdateOrderWithNoProducts() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        
        savedOrder.setProducts(new ArrayList<>());
        orderDao.updateOrder(savedOrder);

        Order updatedOrder = orderDao.getOrderById(savedOrder.getId());
        assertTrue(updatedOrder.getProducts().isEmpty());
    }

    @Test
    public void testGetOrderByNonExistingId() throws SQLException {
        Order fetchedOrder = orderDao.getOrderById(99999L); 
        assertNull(fetchedOrder);
    }

    @Test
    public void testAddExistingProductToOrder() throws SQLException {
        User user = createUser("Test User", "testuser@example.com");
        Product product1 = createProduct("Product 1", 10.00);

        Order order = new Order.Builder()
                .withUser(user)
                .withProducts(List.of(product1))
                .build();

        Order savedOrder = orderDao.saveOrder(order);

        
        orderDao.addProductsToOrder(savedOrder.getId(), List.of(product1));

        Order updatedOrder = orderDao.getOrderById(savedOrder.getId());
        
        assertEquals(1, updatedOrder.getProducts().size());
    }

    
    private User createUser(String name, String email) throws SQLException {
        User user = new User.Builder()
                .withName(name)
                .withEmail(email)
                .build();
        return new UserDaoImpl().saveUser(user);
    }

    private Product createProduct(String name, double price) throws SQLException {
        Product product = new Product.Builder()
                .withName(name)
                .withPrice(price)
                .build();
        return new ProductDaoImpl().saveProduct(product);
    }
}

