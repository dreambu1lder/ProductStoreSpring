package productstore.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productstore.service.UserService;
import productstore.service.apierror.UserNotFoundException;
import productstore.servlet.dto.input.UserInputDTO;
import productstore.servlet.dto.output.UserOutputDTO;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class UserServletTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;
    private UserServlet userServlet;
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        userServlet = new UserServlet(userService);
    }

    @Test
    public void testDoGet_allUsers() throws Exception {
        when(request.getPathInfo()).thenReturn("/");

        List<UserOutputDTO> users = new ArrayList<>();
        users.add(new UserOutputDTO());
        when(userService.getUsersWithPagination(anyInt(), anyInt())).thenReturn(users);

        userServlet.doGet(request, response);

        verify(userService, times(1)).getUsersWithPagination(anyInt(), anyInt());
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("["));
    }

    @Test
    public void testDoGet_userById() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        UserOutputDTO user = new UserOutputDTO();
        when(userService.getUserById(1L)).thenReturn(user);

        userServlet.doGet(request, response);

        verify(userService, times(1)).getUserById(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{"));
    }

    @Test
    public void testDoGet_userNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");

        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException("User not found"));

        userServlet.doGet(request, response);

        verify(userService, times(1)).getUserById(999L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("User not found"));
    }

    @Test
    public void testDoPost_createUser() throws Exception {
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setName("Sample User");

        
        String jsonRequest = gson.toJson(userInputDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        UserOutputDTO createdUser = new UserOutputDTO();
        when(userService.createUser(any(UserInputDTO.class))).thenReturn(createdUser);

        userServlet.doPost(request, response);

        verify(userService, times(1)).createUser(any(UserInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("{"));
    }

    @Test
    public void testDoPost_emptyBody() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        userServlet.doPost(request, response);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Request body is required"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_invalidJsonFormat() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{invalidJson}")));

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid JSON format"));
    }

    @Test
    public void testDoPut_updateUser() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new UserInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        doNothing().when(userService).updateUser(any(UserInputDTO.class));

        userServlet.doPut(request, response);

        verify(userService, times(1)).updateUser(any(UserInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoPut_userNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        String jsonRequest = gson.toJson(new UserInputDTO());
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        doThrow(new UserNotFoundException("User not found")).when(userService).updateUser(any(UserInputDTO.class));

        userServlet.doPut(request, response);

        verify(userService, times(1)).updateUser(any(UserInputDTO.class));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("User not found"));
    }

    @Test
    public void testDoPut_invalidJsonFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{invalidJson}")));

        userServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid JSON format"));
    }

    @Test
    public void testDoDelete_deleteUser() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doNothing().when(userService).deleteUser(1L);

        userServlet.doDelete(request, response);

        verify(userService, times(1)).deleteUser(1L);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void testDoDelete_userNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");

        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(999L);

        userServlet.doDelete(request, response);

        verify(userService, times(1)).deleteUser(999L);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("User not found"));
    }

    @Test
    public void testDoGet_invalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalidpath");

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid user ID format"));
    }

    @Test
    public void testDoPut_invalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalidId");

        userServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid user ID format"));
    }
}
