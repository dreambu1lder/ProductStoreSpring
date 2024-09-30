package productstore.controller.dto.output;

import java.util.List;

public class OrderOutputDTO {
    private long id;
    private UserOutputDTO user;
    private List<ProductOutputDTO> products;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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