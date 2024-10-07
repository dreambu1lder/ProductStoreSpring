package productstore.controller.dto.output;

import java.util.List;

public class OrderOutputDTO {

    private Long id;
    private UserOutputDTO user;
    private List<ProductOutputDTO> products;

    public OrderOutputDTO() {}

    public OrderOutputDTO(Long id, UserOutputDTO user, List<ProductOutputDTO> products) {
        this.id = id;
        this.user = user;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserOutputDTO getUser() {
        return user;
    }

    public void setUser(UserOutputDTO user) {
        this.user = user;
    }

    public List<ProductOutputDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOutputDTO> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "OrderOutputDTO{" +
                "id=" + id +
                ", user=" + user +
                ", products=" + products +
                '}';
    }
}
