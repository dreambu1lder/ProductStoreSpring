package productstore.controller.dto.output;

import java.util.ArrayList;
import java.util.List;

public class UserOutputDTO {

    private Long id;
    private String name;
    private String email;
    private List<Long> orderOutputDTOS = new ArrayList<>();

    public UserOutputDTO() {}

    public UserOutputDTO(Long id, String name, String email) {
        this.id = id;
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

    public List<Long> getOrderOutputDTOS() {
        return orderOutputDTOS;
    }

    public void setOrderOutputDTOS(List<Long> orderOutputDTOS) {
        this.orderOutputDTOS = orderOutputDTOS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserOutputDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", orderOutputDTOS=" + orderOutputDTOS +
                '}';
    }
}
