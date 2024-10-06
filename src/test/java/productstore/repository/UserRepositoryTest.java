package productstore.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import productstore.config.TestDataSourceConfig;
import productstore.model.Order;
import productstore.model.Product;
import productstore.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDataSourceConfig.class)
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User("Test User", "test@example.com");
        User savedUser = userRepository.save(user);

        // Assertions
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserById() {
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).orElse(null);

        // Assertions
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    void shouldUpdateUserEmail() {
        User user = new User("Test User", "old@example.com");
        user = userRepository.save(user);

        // Update email
        user.setEmail("new@example.com");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        // Assertions
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldDeleteUserAndOrders() {
        User user = new User("Test User", "test@example.com");

        // Создаем заказ и добавляем его в список заказов пользователя
        Order order = new Order(user);
        user.getOrders().add(order);

        // Сохраняем пользователя и каскадно сохраняем его заказы
        userRepository.save(user);

        // Удаляем пользователя
        userRepository.delete(user);

        // Проверки
        assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
        assertThat(orderRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRetrieveAllUsersWithOrders() {
        // Создаем и сохраняем продукт
        Product product = new Product("Test Product", 100.0);
        product = productRepository.save(product);

        // Создаем и сохраняем пользователей
        User user1 = new User("User 1", "user1@example.com");
        User user2 = new User("User 2", "user2@example.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // Создаем и сохраняем заказы, связанные с пользователями и продуктами
        Order order1 = new Order(user1);
        order1.setOrderProducts(Collections.singletonList(product));

        // Устанавливаем двухстороннюю связь
        user1.getOrders().add(order1);

        orderRepository.save(order1);

        Order order2 = new Order(user2);
        order2.setOrderProducts(Collections.singletonList(product));

        // Устанавливаем двухстороннюю связь
        user2.getOrders().add(order2);

        orderRepository.save(order2);

        // Загружаем всех пользователей и инициализируем их заказы
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> user.getOrders().size()); // Явно инициализируем заказы

        // Assertions
        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(allUsers.get(0).getOrders()).isNotEmpty();
        assertThat(allUsers.get(1).getOrders()).isNotEmpty();
    }

    @Test
    void shouldSaveUserWithMultipleOrders() {
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        // Создаем заказы и добавляем их в пользователя
        Order order1 = new Order(user);
        Order order2 = new Order(user);
        user.getOrders().add(order1);
        user.getOrders().add(order2);

        // Сохраняем пользователя, чтобы заказы также были сохранены
        userRepository.save(user);

        // Загружаем пользователя снова из репозитория
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        // Проверка
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getOrders().size()).isEqualTo(2);
    }
}
