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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDataSourceConfig.class)
@ActiveProfiles("test")
@Transactional
public class OrderRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Тест сохранения и получения заказа с продуктами
    @Test
    void shouldSaveAndRetrieveOrder() {
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        Product product = new Product("Test Product", 100.0);
        productRepository.save(product);

        Order order = new Order(user);
        order.setOrderProducts(Collections.singletonList(product));
        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getUser()).isEqualTo(user);
        assertThat(savedOrder.getOrderProducts().size()).isEqualTo(1);
    }

    // Тест удаления заказа, но не связанных продуктов
    @Test
    void shouldDeleteOrderAndRetainProducts() {
        Product product = new Product("Test Product", 100.0);
        productRepository.save(product);

        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        Order order = new Order(user);
        order.setOrderProducts(Collections.singletonList(product));
        orderRepository.save(order);

        orderRepository.delete(order);

        // Убедитесь, что продукт не был удален после удаления заказа
        assertThat(productRepository.findAll().size()).isEqualTo(1);
        assertThat(orderRepository.findAll().size()).isEqualTo(0);
    }

    // Тест обновления заказа и проверки связанных продуктов
    @Test
    void shouldUpdateOrderWithAdditionalProducts() {
        // Given
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        Product product1 = new Product("Test Product 1", 50.0);
        Product product2 = new Product("Test Product 2", 150.0);
        productRepository.save(product1);
        productRepository.save(product2);

        Order order = new Order(user);
        order.setOrderProducts(Collections.singletonList(product1));
        Order savedOrder = orderRepository.save(order);

        // When
        savedOrder.setOrderProducts(Arrays.asList(product1, product2));
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertThat(updatedOrder.getOrderProducts().size()).isEqualTo(2);
        assertThat(updatedOrder.getOrderProducts()).containsExactlyInAnyOrder(product1, product2);
    }

    // Тест получения заказа по ID
    @Test
    void shouldFindOrderById() {
        // Given
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        Product product = new Product("Test Product", 100.0);
        productRepository.save(product);

        Order order = new Order(user);
        order.setOrderProducts(Collections.singletonList(product));
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder.isPresent()).isTrue();
        assertThat(foundOrder.get().getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.get().getUser()).isEqualTo(user);
        assertThat(foundOrder.get().getOrderProducts()).containsExactly(product);
    }

    // Тест получения всех заказов
    @Test
    void shouldFindAllOrders() {
        // Given
        User user1 = new User("User1", "user1@example.com");
        User user2 = new User("User2", "user2@example.com");
        userRepository.saveAll(Arrays.asList(user1, user2));

        Product product1 = new Product("Product1", 100.0);
        Product product2 = new Product("Product2", 200.0);
        productRepository.saveAll(Arrays.asList(product1, product2));

        Order order1 = new Order(user1);
        order1.setOrderProducts(Collections.singletonList(product1));

        Order order2 = new Order(user2);
        order2.setOrderProducts(Collections.singletonList(product2));

        orderRepository.saveAll(Arrays.asList(order1, order2));

        // When
        List<Order> allOrders = orderRepository.findAll();

        // Then
        assertThat(allOrders.size()).isEqualTo(2);
        assertThat(allOrders.get(0).getUser()).isIn(user1, user2);
        assertThat(allOrders.get(1).getOrderProducts()).containsAnyOf(product1, product2);
    }
}
