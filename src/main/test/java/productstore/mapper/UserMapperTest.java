package productstore.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import productstore.model.Order;
import productstore.model.User;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.servlet.dto.output.UserOutputDTO;
import productstore.servlet.mapper.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    public void testToUser() {
        
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setId(1L);
        userInputDTO.setName("Test User");

        
        User user = userMapper.toUser(userInputDTO);

        
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());

        
        assertNull(user.getOrders());
    }

    @Test
    public void testToUserOutputDTO_withIncludeOrderIds() {
        
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Order order1 = new Order();
        order1.setId(101L);
        Order order2 = new Order();
        order2.setId(102L);

        user.setOrders(List.of(order1, order2));

        
        UserOutputDTO userOutputDTO = userMapper.toUserOutputDTO(true, user);

        
        assertEquals(1L, userOutputDTO.getId());
        assertEquals("Test User", userOutputDTO.getName());

        
        assertEquals(List.of(101L, 102L), userOutputDTO.getOrderIds());
    }

    @Test
    public void testToUserOutputDTO_withoutIncludeOrderIds() {
        
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        
        UserOutputDTO userOutputDTO = userMapper.toUserOutputDTO(false, user);

        
        assertEquals(1L, userOutputDTO.getId());
        assertEquals("Test User", userOutputDTO.getName());

        
        assertNull(userOutputDTO.getOrderIds());
    }

    @Test
    public void testOrdersToOrderIds() {
        
        Order order1 = new Order();
        order1.setId(101L);
        Order order2 = new Order();
        order2.setId(102L);

        List<Order> orders = List.of(order1, order2);

        
        List<Long> orderIds = userMapper.ordersToOrderIds(orders);

        
        assertEquals(List.of(101L, 102L), orderIds);
    }

    @Test
    public void testOrdersToOrderIds_withNullOrders() {
        
        List<Long> orderIds = userMapper.ordersToOrderIds(null);

        
        assertTrue(orderIds.isEmpty());
    }
}
