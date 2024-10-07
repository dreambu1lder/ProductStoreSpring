package productstore.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.controller.mapper.UserMapper;
import productstore.model.Order;
import productstore.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserOutputDTO() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        Order order1 = new Order();
        order1.setId(101L);
        Order order2 = new Order();
        order2.setId(102L);
        user.setOrders(Arrays.asList(order1, order2));

        // When
        UserOutputDTO userOutputDTO = userMapper.toDTO(user);

        // Then
        assertNotNull(userOutputDTO);
        assertEquals(1L, userOutputDTO.getId());
        assertEquals("John Doe", userOutputDTO.getName());
        assertEquals("john@example.com", userOutputDTO.getEmail());
        assertEquals(Arrays.asList(101L, 102L), userOutputDTO.getOrderOutputDTOS());
    }

    @Test
    void shouldMapUsersToUserOutputDTOs() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        Order order1 = new Order();
        order1.setId(101L);
        user1.setOrders(Arrays.asList(order1));

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        Order order2 = new Order();
        order2.setId(102L);
        user2.setOrders(Arrays.asList(order2));

        List<User> users = Arrays.asList(user1, user2);

        // When
        List<UserOutputDTO> userOutputDTOs = userMapper.toDTOs(users);

        // Then
        assertNotNull(userOutputDTOs);
        assertEquals(2, userOutputDTOs.size());
        assertEquals(1L, userOutputDTOs.get(0).getId());
        assertEquals(2L, userOutputDTOs.get(1).getId());
        assertEquals(Arrays.asList(101L), userOutputDTOs.get(0).getOrderOutputDTOS());
        assertEquals(Arrays.asList(102L), userOutputDTOs.get(1).getOrderOutputDTOS());
    }

    @Test
    void shouldMapUserInputDTOToUserEntity() {
        // Given
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setName("John Doe");
        userInputDTO.setEmail("john@example.com");

        // When
        User user = userMapper.toEntity(userInputDTO);

        // Then
        assertNotNull(user);
        assertNull(user.getId()); // Проверяем, что id игнорируется при маппинге
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertTrue(user.getOrders() == null || user.getOrders().isEmpty()); // Проверяем, что orders - либо null, либо пустой список
    }
}
