package productstore.controller.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserInputDTO {

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-Я\\s]+$",
            message = "Name can only contain letters and spaces"
    )
    private String name;
    @NotEmpty(message = "Email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    public UserInputDTO() {}

    public UserInputDTO(String name, String email) {
            this.name = name;
            this.email = email;
    }

    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getEmail() {
            return email;
    }

    public void setEmail(String email) {
            this.email = email;
    }

    @Override
    public String toString() {
        return "UserInputDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
