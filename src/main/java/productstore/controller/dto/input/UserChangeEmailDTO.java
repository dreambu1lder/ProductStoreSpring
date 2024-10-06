package productstore.controller.dto.input;

public class UserChangeEmailDTO {

    private String email;

    public UserChangeEmailDTO() {}

    public UserChangeEmailDTO(String email) {
        this.email = email;
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
                ", email='" + email +
                '}';
    }
}
