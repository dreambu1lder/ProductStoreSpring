package productstore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import productstore.controller.dto.input.UserChangeEmailDTO;
import productstore.controller.dto.input.UserInputDTO;
import productstore.controller.dto.output.UserOutputDTO;
import productstore.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {UserController.class})
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private UserInputDTO userInputDTO;
    private UserChangeEmailDTO userChangeEmailDTO;
    private UserOutputDTO userOutputDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userInputDTO = new UserInputDTO();
        userInputDTO.setName("Test User");
        userInputDTO.setEmail("test@example.com");

        userChangeEmailDTO = new UserChangeEmailDTO();
        userChangeEmailDTO.setEmail("new@example.com");

        userOutputDTO = new UserOutputDTO();
        userOutputDTO.setId(1L);
        userOutputDTO.setName("Test User");
        userOutputDTO.setEmail("test@example.com");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserOutputDTO> users = Arrays.asList(userOutputDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(userOutputDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(userOutputDTO.getName()))
                .andExpect(jsonPath("$[0].email").value(userOutputDTO.getEmail()));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userOutputDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(userOutputDTO.getName()))
                .andExpect(jsonPath("$.email").value(userOutputDTO.getEmail()));
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.saveUser(any(UserInputDTO.class))).thenReturn(userOutputDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(userOutputDTO.getName()))
                .andExpect(jsonPath("$.email").value(userOutputDTO.getEmail()));
    }

    @Test
    public void testDeleteUserById() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateUserById() throws Exception {
        userOutputDTO.setEmail(userChangeEmailDTO.getEmail());
        when(userService.updateUserById(anyLong(), any(UserChangeEmailDTO.class))).thenReturn(userOutputDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangeEmailDTO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(userOutputDTO.getId()))
                .andExpect(jsonPath("$.name").value(userOutputDTO.getName()))
                .andExpect(jsonPath("$.email").value(userChangeEmailDTO.getEmail()));
    }
}
