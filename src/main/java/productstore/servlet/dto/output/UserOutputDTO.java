package productstore.servlet.dto.output;

import java.util.ArrayList;
import java.util.List;

public class UserOutputDTO {

    private long id;
    private String name;
    private String email;
    private List<Long> orderIds = new ArrayList<>();

    public UserOutputDTO() {}

    public UserOutputDTO(long id, String name, String email, List<Long> orderIds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.orderIds = orderIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    @Override
    public String toString() {
        return "UserOutputDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", orderIds=" + orderIds +
                '}';
    }
}
