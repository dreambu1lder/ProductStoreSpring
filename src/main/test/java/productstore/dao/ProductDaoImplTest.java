package productstore.dao;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import productstore.dao.impl.UserDaoImpl;
import productstore.dao.utils.PostgreSQLContainerProvider;
import productstore.dao.impl.ProductDaoImpl;
import productstore.db.DataBaseUtil;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;


import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ProductDaoImplTest {
    private ProductDao productDao;

    @BeforeAll
    public static void setUpDatabase() {
        
        PostgreSQLContainerProvider.startContainer();
    }

    @BeforeEach
    public void setUp() {
        productDao = new ProductDaoImpl();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders_products CASCADE;");
            stmt.execute("TRUNCATE TABLE products CASCADE;");
            stmt.execute("TRUNCATE TABLE orders CASCADE;");
        }
    }

    @AfterAll
    public static void tearDownAll() {
        
        PostgreSQLContainerProvider.stopContainer();
    }

    @Test
    public void testSaveProduct() throws SQLException {
        Product product = new Product.Builder().withName("Test Product").withPrice(99.99).build();
        Product savedProduct = productDao.saveProduct(product);

        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("Test Product", savedProduct.getName());
        assertEquals(99.99, savedProduct.getPrice());
    }

    @Test
    public void testGetProductById() throws SQLException {
        Product product = new Product.Builder().withName("Test Product").withPrice(99.99).build();
        Product savedProduct = productDao.saveProduct(product);

        Product fetchedProduct = productDao.getProductById(savedProduct.getId());
        assertNotNull(fetchedProduct);
        assertEquals(savedProduct.getId(), fetchedProduct.getId());
        assertEquals(savedProduct.getName(), fetchedProduct.getName());
    }

    @Test
    public void testGetProductWithPagination() throws SQLException {
        
        for (int i = 1; i <= 10; i++) {
            Product product = new Product.Builder().withName("Product " + i).withPrice(50.0 + i).build();
            productDao.saveProduct(product);
        }

        
        List<Product> firstPage = productDao.getProductWithPagination(1, 5);
        List<Product> secondPage = productDao.getProductWithPagination(2, 5);

        assertEquals(5, firstPage.size());
        assertEquals(5, secondPage.size());
        assertEquals("Product 1", firstPage.get(0).getName());
        assertEquals("Product 6", secondPage.get(0).getName());
    }

    @Test
    public void testUpdateProduct() throws SQLException {
        Product product = new Product.Builder().withName("Test Product").withPrice(99.99).build();
        Product savedProduct = productDao.saveProduct(product);

        
        savedProduct.setName("Updated Product");
        savedProduct.setPrice(79.99);
        productDao.updateProduct(savedProduct);

        Product updatedProduct = productDao.getProductById(savedProduct.getId());
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(79.99, updatedProduct.getPrice());
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        Product product = new Product.Builder().withName("Test Product").withPrice(99.99).build();
        Product savedProduct = productDao.saveProduct(product);

        
        productDao.deleteProduct(savedProduct.getId());

        Product fetchedProduct = productDao.getProductById(savedProduct.getId());
        assertNull(fetchedProduct);
    }

    @Test
    public void testGetAllProducts() throws SQLException {
        
        for (int i = 1; i <= 5; i++) {
            Product product = new Product.Builder().withName("Product " + i).withPrice(50.0 + i).build();
            productDao.saveProduct(product);
        }

        List<Product> products = productDao.getAllProducts();
        assertEquals(5, products.size());
    }

    @Test
    public void testGetProductByNonExistingId() throws SQLException {
        
        Product product = productDao.getProductById(9999L);
        assertNull(product, "Ожидается, что продукт не будет найден.");
    }

    @Test
    public void testGetAllProductsFromEmptyDatabase() throws SQLException {
        
        List<Product> products = productDao.getAllProducts();
        assertTrue(products.isEmpty(), "Ожидается, что список продуктов будет пустым.");
    }

    @Test
    public void testDeleteNonExistingProduct() throws SQLException {
        
        productDao.deleteProduct(9999L); 
        
    }

    @Test
    public void testSaveProductWithInvalidData() {
        
        Product product = new Product.Builder().withName("Invalid Product").withPrice(-50.0).build();

        assertThrows(SQLException.class, () -> {
            productDao.saveProduct(product);
        }, "Ожидается, что сохранение продукта с отрицательной ценой вызовет исключение.");
    }

    @Test
    public void testUpdateProductWithNewOrders() throws SQLException {
        
        User user = createUser("Test User", "testuser@example.com");

        
        Order order1 = new Order.Builder().withUser(user).build();
        Order order2 = new Order.Builder().withUser(user).build();

        
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO orders (user_id) VALUES (?) RETURNING id;")) {

            
            stmt.setLong(1, user.getId());
            ResultSet rs1 = stmt.executeQuery();
            if (rs1.next()) {
                order1.setId(rs1.getLong(1));
            }

            
            stmt.setLong(1, user.getId());
            ResultSet rs2 = stmt.executeQuery();
            if (rs2.next()) {
                order2.setId(rs2.getLong(1));
            }
        }

        
        Product product = new Product.Builder().withName("Test Product").withPrice(99.99).build();
        productDao.saveProduct(product);

        
        product.setOrders(List.of(order1, order2));
        productDao.updateProduct(product);

        
        Product updatedProduct = productDao.getProductWithOrdersById(product.getId());
        assertNotNull(updatedProduct);
        assertEquals(2, updatedProduct.getOrders().size());
    }

    
    private User createUser(String name, String email) throws SQLException {
        User user = new User.Builder()
                .withName(name)
                .withEmail(email)
                .build();
        return new UserDaoImpl().saveUser(user);
    }
}
