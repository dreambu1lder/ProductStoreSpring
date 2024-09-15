package productstore.servlet.dto.output;

import java.util.List;

public class OrderOutputDTO {
    private long id;
    private long userId;
    private List<ProductOutputDTO> products;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
                ", userId=" + userId +
                ", products=" + products +
                '}';
    }
}