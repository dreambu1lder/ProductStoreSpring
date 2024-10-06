package productstore.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import productstore.config.TestDataSourceConfig;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDataSourceConfig.class)
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        Product product = new Product("Test Product", 100.0);
        Product savedProduct = productRepository.save(product);

        // Assertions
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(100.0);
    }

    @Test
    void shouldFindProductById() {
        Product product = new Product("Test Product", 100.0);
        productRepository.save(product);

        Product foundProduct = productRepository.findById(product.getId()).orElse(null);

        // Assertions
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(product.getId());
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
    }

    @Test
    void shouldUpdateProductDetails() {
        Product product = new Product("Old Product", 100.0);
        product = productRepository.save(product);

        // Update product details
        product.setName("Updated Product");
        product.setPrice(150.0);
        productRepository.save(product);

        Product updatedProduct = productRepository.findById(product.getId()).orElse(null);

        // Assertions
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(150.0);
    }

    @Test
    void shouldDeleteProductAndNotAffectOrders() {
        // Создаем и сохраняем продукт
        Product product = new Product("Test Product", 100.0);
        product = productRepository.save(product);

        // Создаем и сохраняем пользователя
        User user = new User("Test User", "test@example.com");
        user = userRepository.save(user);

        // Создаем и сохраняем заказ, связанный с продуктом
        Order order = new Order(user);
        order.setOrderProducts(Collections.singletonList(product));
        order = orderRepository.save(order);

        // Проверяем, что продукт связан с заказом
        assertThat(order.getOrderProducts()).contains(product);

        // Удаляем продукт из заказов
        List<Order> ordersWithProduct = orderRepository.findAllByOrderProductsContaining(product);
        for (Order o : ordersWithProduct) {
            o.getOrderProducts().remove(product);
            orderRepository.save(o); // Сохраняем изменения в заказе
        }

        // Теперь можно удалить продукт из репозитория
        productRepository.delete(product);

        // Проверить, что продукт удален из репозитория
        List<Product> allProducts = productRepository.findAll();
        assertThat(allProducts).doesNotContain(product);

        // Проверить, что заказ все еще существует и не затронут
        Order foundOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderProducts()).doesNotContain(product);
    }

    @Test
    void shouldAddProductToMultipleOrders() {
        Product product = new Product("Shared Product", 200.0);
        product = productRepository.save(product);

        User user1 = new User("User 1", "user1@example.com");
        User user2 = new User("User 2", "user2@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        Order order1 = new Order(user1);
        order1.setOrderProducts(Collections.singletonList(product));
        product.getOrders().add(order1); // Двусторонняя ассоциация
        orderRepository.save(order1);

        Order order2 = new Order(user2);
        order2.setOrderProducts(Collections.singletonList(product));
        product.getOrders().add(order2); // Двусторонняя ассоциация
        orderRepository.save(order2);

        // Загружаем продукт из базы данных
        Product foundProduct = productRepository.findById(product.getId()).orElse(null);

        // Проверяем, что продукт связан с двумя заказами
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getOrders().size()).isEqualTo(2); // Ожидается 2 заказа
    }

    @Test
    void shouldRetrieveAllProductsWithOrders() {
        Product product1 = new Product("Product 1", 50.0);
        Product product2 = new Product("Product 2", 100.0);
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);

        User user = new User("Test User", "user@example.com");
        user = userRepository.save(user);

        Order order1 = new Order(user);
        order1.setOrderProducts(Collections.singletonList(product1));
        product1.getOrders().add(order1); // Двусторонняя ассоциация
        orderRepository.save(order1);

        Order order2 = new Order(user);
        order2.setOrderProducts(Collections.singletonList(product2));
        product2.getOrders().add(order2); // Двусторонняя ассоциация
        orderRepository.save(order2);

        List<Product> allProducts = productRepository.findAll();

        // Логгирование для отладки
        allProducts.forEach(product -> System.out.println("Product: " + product.getName() + ", Orders: " + product.getOrders()));

        // Assertions
        assertThat(allProducts.size()).isEqualTo(2);
        assertThat(allProducts).extracting(Product::getName).containsExactlyInAnyOrder("Product 1", "Product 2");
        assertThat(allProducts.get(0).getOrders()).isNotEmpty();
        assertThat(allProducts.get(1).getOrders()).isNotEmpty();
    }
}
