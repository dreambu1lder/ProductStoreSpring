package productstore.dao;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import productstore.dao.impl.UserDaoImpl;
import productstore.dao.utils.PostgreSQLContainerProvider;
import productstore.db.DataBaseUtil;
import productstore.model.Order;
import productstore.model.User;


import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoImplTest {
    private UserDao userDao;

    @BeforeAll
    public static void setUpDatabase() {
        
        PostgreSQLContainerProvider.startContainer();
    }

    @BeforeEach
    public void setUp() {
        userDao = new UserDaoImpl();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders CASCADE;");
            stmt.execute("TRUNCATE TABLE users CASCADE;");
        }
    }

    @AfterAll
    public static void tearDownAll() {
        
        PostgreSQLContainerProvider.stopContainer();
    }

    @Test
    public void testSaveUser() throws SQLException {
        User user = new User.Builder().withName("John Doe").withEmail("john@example.com").build();
        User savedUser = userDao.saveUser(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    public void testGetUserById() throws SQLException {
        User user = new User.Builder().withName("John Doe").withEmail("john@example.com").build();
        User savedUser = userDao.saveUser(user);

        User fetchedUser = userDao.getUserById(savedUser.getId());
        assertNotNull(fetchedUser);
        assertEquals(savedUser.getId(), fetchedUser.getId());
        assertEquals(savedUser.getName(), fetchedUser.getName());
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        
        for (int i = 1; i <= 3; i++) {
            User user = new User.Builder().withName("User " + i).withEmail("user" + i + "@example.com").build();
            userDao.saveUser(user);
        }

        List<User> users = userDao.getAllUsers();
        assertEquals(3, users.size());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User.Builder().withName("John Doe").withEmail("john@example.com").build();
        User savedUser = userDao.saveUser(user);

        
        savedUser.setName("John Smith");
        userDao.updateUser(savedUser);

        User updatedUser = userDao.getUserById(savedUser.getId());
        assertEquals("John Smith", updatedUser.getName());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        User user = new User.Builder().withName("John Doe").withEmail("john@example.com").build();
        User savedUser = userDao.saveUser(user);

        
        userDao.deleteUser(savedUser.getId());

        User fetchedUser = userDao.getUserById(savedUser.getId());
        assertNull(fetchedUser);
    }

    @Test
    public void testGetUserWithPagination() throws SQLException {
        
        for (int i = 1; i <= 10; i++) {
            User user = new User.Builder().withName("User " + i).withEmail("user" + i + "@example.com").build();
            userDao.saveUser(user);
        }

        
        List<User> firstPage = userDao.getUserWithPagination(1, 5);
        List<User> secondPage = userDao.getUserWithPagination(2, 5);

        assertEquals(5, firstPage.size());
        assertEquals(5, secondPage.size());
        assertEquals("User 1", firstPage.get(0).getName());
        assertEquals("User 6", secondPage.get(0).getName());
    }

    @Test
    public void testSaveUserWithEmptyNameOrEmail() {
        User userWithEmptyName = new User.Builder().withName("").withEmail("test@example.com").build();
        User userWithEmptyEmail = new User.Builder().withName("John Doe").withEmail("").build();

        
        assertThrows(SQLException.class, () -> {
            userDao.saveUser(userWithEmptyName);
        }, "Ожидается, что сохранение пользователя с пустым именем вызовет исключение.");

        assertThrows(SQLException.class, () -> {
            userDao.saveUser(userWithEmptyEmail);
        }, "Ожидается, что сохранение пользователя с пустым email вызовет исключение.");
    }

    @Test
    public void testGetUserByNonExistentId() throws SQLException {
        User user = userDao.getUserById(9999L); 
        assertNull(user, "Ожидается, что попытка получить пользователя с несуществующим ID вернет null.");
    }

    @Test
    public void testGetAllUsersWhenEmpty() throws SQLException {
        List<User> users = userDao.getAllUsers();
        assertTrue(users.isEmpty(), "Ожидается, что при пустой таблице пользователей метод getAllUsers вернет пустой список.");
    }

    @Test
    public void testGetUserWithPaginationWhenEmpty() throws SQLException {
        List<User> users = userDao.getUserWithPagination(1, 5);
        assertTrue(users.isEmpty(), "Ожидается, что при пустой таблице пользователей метод getUserWithPagination вернет пустой список.");
    }

    @Test
    public void testGetUserWithPaginationEdgeCases() throws SQLException {
        
        for (int i = 1; i <= 3; i++) {
            User user = new User.Builder().withName("User " + i).withEmail("user" + i + "@example.com").build();
            userDao.saveUser(user);
        }

        
        List<User> users = userDao.getUserWithPagination(1, 10);
        assertEquals(3, users.size(), "Ожидается, что метод вернет всех пользователей, если запрашивается больше, чем есть в базе.");

        
        users = userDao.getUserWithPagination(2, 5);
        assertTrue(users.isEmpty(), "Ожидается, что метод вернет пустой список, если запрашивается страница за пределами диапазона.");
    }

    @Test
    public void testUpdateNonExistentUser() throws SQLException {
        User nonExistentUser = new User.Builder().withId(9999L).withName("Non-existent").withEmail("nonexistent@example.com").build();

        
        
        userDao.updateUser(nonExistentUser);
        User user = userDao.getUserById(9999L);
        assertNull(user, "Ожидается, что обновление несуществующего пользователя не изменит базу данных.");
    }

    @Test
    public void testDeleteUserAndRelatedOrders() throws SQLException {
        
        User user = new User.Builder().withName("John Doe").withEmail("john@example.com").build();
        user = userDao.saveUser(user);

        Order order = new Order.Builder().withUser(user).build();
        try (Connection connection = DataBaseUtil.getConnection()) {
            String insertOrderSQL = "INSERT INTO orders (user_id) VALUES (?) RETURNING id;";
            try (PreparedStatement stmt = connection.prepareStatement(insertOrderSQL)) {
                stmt.setLong(1, user.getId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }
        }

        
        userDao.deleteUser(user.getId());

        
        User fetchedUser = userDao.getUserById(user.getId());
        assertNull(fetchedUser, "Ожидается, что после удаления пользователь не будет найден.");

        try (Connection connection = DataBaseUtil.getConnection()) {
            String checkOrderSQL = "SELECT * FROM orders WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkOrderSQL)) {
                stmt.setLong(1, order.getId());
                ResultSet rs = stmt.executeQuery();
                assertFalse(rs.next(), "Ожидается, что после удаления пользователя его заказы также удалены.");
            }
        }
    }
}
