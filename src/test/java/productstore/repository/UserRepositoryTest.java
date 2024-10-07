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

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserById() {
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    void shouldUpdateUserEmail() {
        User user = new User("Test User", "old@example.com");
        user = userRepository.save(user);

        user.setEmail("new@example.com");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldDeleteUserAndOrders() {
        User user = new User("Test User", "test@example.com");

        Order order = new Order(user);
        user.getOrders().add(order);

        userRepository.save(user);

        userRepository.delete(user);

        assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
        assertThat(orderRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRetrieveAllUsersWithOrders() {
        Product product = new Product("Test Product", 100.0);
        product = productRepository.save(product);

        User user1 = new User("User 1", "user1@example.com");
        User user2 = new User("User 2", "user2@example.com");
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        Order order1 = new Order(user1);
        order1.setOrderProducts(Collections.singletonList(product));

        user1.getOrders().add(order1);

        orderRepository.save(order1);

        Order order2 = new Order(user2);
        order2.setOrderProducts(Collections.singletonList(product));

        user2.getOrders().add(order2);

        orderRepository.save(order2);

        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> user.getOrders().size());

        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(allUsers.get(0).getOrders()).isNotEmpty();
        assertThat(allUsers.get(1).getOrders()).isNotEmpty();
    }

    @Test
    void shouldSaveUserWithMultipleOrders() {
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        Order order1 = new Order(user);
        Order order2 = new Order(user);
        user.getOrders().add(order1);
        user.getOrders().add(order2);

        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getOrders().size()).isEqualTo(2);
    }
}
